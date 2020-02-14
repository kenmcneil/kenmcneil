package com.ferguson.cs.product.task.inventory.batch;

import java.io.File;

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

import com.ferguson.cs.product.task.inventory.FileTransferProperties;
import com.ferguson.cs.product.task.inventory.ManhattanInboundSettings;
import com.ferguson.cs.product.task.inventory.ManhattanInventoryProcessorConfiguration.ManhattanOutboundGateway;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

public class FileHandlingTasklet implements Tasklet {

	private static final Logger log = LoggerFactory.getLogger(FileHandlingTasklet.class);

	private ManhattanInventoryJob manhattanInventoryJob;
	private ManhattanOutboundGateway manhattanOutboundGateway;
	private ManhattanInboundSettings manhattanInboundSettings;

	public FileHandlingTasklet(ManhattanInventoryJob manhattanInventoryJob, ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInventoryJob = manhattanInventoryJob;
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		FileTransferProperties fileTransferProperties = manhattanInboundSettings.getFileTransferProperties()
				.get(manhattanInventoryJob.getManhattanChannel().getStringValue());

		String filePath = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.getString("filePath");
		File file = new File(filePath);
		if (fileTransferProperties.getUploadFile()) {
			ftpUploadFile(file);
		}

		if (fileTransferProperties.getStoreFile()) {
			FileUtils.copyFileToDirectory(file, new File(fileTransferProperties.getStoragePath()));
		}
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setManhattanOutboundGateway(ManhattanOutboundGateway manhattanOutboundGateway) {
		this.manhattanOutboundGateway = manhattanOutboundGateway;
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
	private void ftpUploadFile(File file) {
		log.debug("Started Uploading manhattan {} :{}", manhattanInventoryJob.getManhattanChannel()
				.getStringValue(), file.getName());
		if (ManhattanChannel.SUPPLY == manhattanInventoryJob.getManhattanChannel()) {
			manhattanOutboundGateway.sendManhattanSupplyFileSftp(file);
		} else if (ManhattanChannel.HMWALLACE == manhattanInventoryJob.getManhattanChannel()) {
			manhattanOutboundGateway.sendManhattanHmWallaceFileSftp(file);
		}
		log.debug("Uploaded file");
	}
}
