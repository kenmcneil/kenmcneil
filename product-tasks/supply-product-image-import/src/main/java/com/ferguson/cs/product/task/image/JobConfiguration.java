package com.ferguson.cs.product.task.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.product.task.image.decider.DeciderDirectoryHasImportFiles;
import com.ferguson.cs.product.task.image.integration.webservices.WebservicesClient;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;



@Configuration
public class JobConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobConfiguration.class);

	private final TaskBatchJobFactory taskBatchJobFactory;

	private final TaskConfiguration taskConfiguration;

	private final WebservicesClient wsClient;

	public JobConfiguration(TaskBatchJobFactory taskBatchJobFactory, TaskConfiguration taskConfiguration,
			WebservicesClient wsClient) {
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.taskConfiguration = taskConfiguration;
		this.wsClient = wsClient;
	}

	@Bean(name = "supply-product-image-import-job")
	public Job productImageImportJob() {

		LOGGER.info("Creating product image import job bean ...");

		Step jobStartStep = null;
		try {
			jobStartStep = getStartStep();
		} catch (Exception e) {
			final RuntimeException rethrow = new RuntimeException(
					"Error getting start step bean while building task job.", e);
			LOGGER.error(rethrow.getMessage(), rethrow);
			throw rethrow;
		}

		return taskBatchJobFactory.getJobBuilder("supply-product-image-import-job").incrementer(new RunIdIncrementer())
				.listener(new JobListener()).start(jobStartStep).build();

	}

	@Bean
	public Step getStartStep() {

		LOGGER.info("Creating start step bean for job ...");

		Flow jobFlow = null;
		try {
			jobFlow = getJobFlow();
		} catch (Exception e) {
			throw new RuntimeException("Error getting job flow while building task job.", e);
		}

		final StartStepListener startStepListener = new StartStepListener();

		final Step startStep = taskBatchJobFactory.getStepBuilder("getStartStep").listener(startStepListener)
				.flow(jobFlow).build();

		return startStep;

	}

	@Bean
	public Flow getJobFlow() {

		LOGGER.info("Creating job flow bean ..");


		Flow flow;

		final JobExecutionDecider hasImportFilesDecider = new DeciderDirectoryHasImportFiles(
				this.taskConfiguration.supplySessionFactory(),
				this.taskConfiguration.supplyImageImportFtp().getBaseFilePath());

		final Step importSupplyProductImageFilesStep = taskBatchJobFactory.getStepBuilder("rename-files-step")
				.tasklet(new SupplyProductImageImportTasklet(this.taskConfiguration.supplySessionFactory(),
						this.taskConfiguration.supplyImageImportFtp(), this.wsClient))
				.build();

		try {

			flow = new FlowBuilder<Flow>("getJobFlow").start(hasImportFilesDecider).on("false").end("NO_FTP_PROCESSING")
					.on("true").to(importSupplyProductImageFilesStep).build();

		} catch (Exception e) {
			throw new RuntimeException("Error building job flow.", e);
		}

		return flow;
	}

}
