package com.ferguson.cs.product.task.wiser.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.WiserFeedSettings;
import com.ferguson.cs.product.task.wiser.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.wiser.model.FileType;
import com.ferguson.cs.utilities.DateUtils;

public class UploadCostTasklet implements Tasklet {

	private BuildWebServicesFeignClient client;
	private WiserFeedSettings wiserFeedSettings;
	private static final Integer UPLOAD_USER_ID = 835;
	private static final Logger LOG = LoggerFactory.getLogger(UploadCostTasklet.class);

	@Autowired
	public void setClient(BuildWebServicesFeignClient client) {
		this.client = client;
	}

	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		String localFileName = jobExecutionContext.getString("fileName");
		LOG.debug("Starting cost upload job");
		String dateString = DateUtils.dateToIsoString(new Date());
		client.createCostUploadJob(localFileName, FileType.PRICEBOOK_CSV,dateString,UPLOAD_USER_ID);
		LOG.debug("Started cost upload job");
		return RepeatStatus.FINISHED;
	}

}