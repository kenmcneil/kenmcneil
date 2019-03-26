package com.ferguson.cs.product.task.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class JobListener implements JobExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobListener.class);

	@Override
	public void beforeJob(JobExecution jobExecution) {
		LOGGER.info(String.format("JobListener before job event invoked ... job-name: ",
				jobExecution.getJobInstance().getJobName()));
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		LOGGER.info(String.format("JobListener after job event invoked ... job-name: ",
				jobExecution.getJobInstance().getJobName()));
	}

}