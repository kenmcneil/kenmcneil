package com.ferguson.cs.product.task.supply;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import com.ferguson.cs.product.task.supply.SupplyProductDataFeedConfiguration.SupplyGateway;

public class FileUploadTasklet implements Tasklet {

	private static final Logger log = LoggerFactory.getLogger(FileUploadTasklet.class);

	private SupplyGateway supplyGateway;
	private SupplyProductDataFeedSettings supplyProductDataFeedSettings;

	@Autowired
	public void setSupplyGateway(SupplyGateway supplyGateway) {
		this.supplyGateway = supplyGateway;
	}

	@Autowired
	public void setSupplyProductDataFeedSettings(SupplyProductDataFeedSettings supplyProductDataFeedSettings) {
		this.supplyProductDataFeedSettings = supplyProductDataFeedSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		if(supplyProductDataFeedSettings.getFilePath() != null) {
			File directory = new File(supplyProductDataFeedSettings.getFilePath());
			File[] files = directory.listFiles();
			if(files != null) {
				if (directory.isDirectory()) {
					for (File file : files) {
						ftpUploadFile(file);
					}
				}
				return RepeatStatus.FINISHED;
			}

		}
		throw new FileNotFoundException("Could not find supply upload directory");
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
	private void ftpUploadFile(File file) {
		log.debug("Started Uploading file to Supply" + file.getName());
		supplyGateway.sendSupplyFileSftp(file);
		log.debug("Uploaded file to Supply");
		FileUtils.deleteQuietly(file);
	}
}
