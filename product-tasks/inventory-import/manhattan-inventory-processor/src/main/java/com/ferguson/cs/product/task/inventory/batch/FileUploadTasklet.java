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
import com.ferguson.cs.product.task.inventory.ManhattanInventoryProcessorConfiguration.ManhattanOutboundGateway;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

public class FileUploadTasklet implements Tasklet {

	private static final Logger log = LoggerFactory.getLogger(FileUploadTasklet.class);

	private ManhattanInventoryJob manhattanInventoryJob;
	private ManhattanOutboundGateway manhattanOutboundGateway;
	private String filePath;


	public FileUploadTasklet(ManhattanInventoryJob manhattanInventoryJob,String filePath) {
		this.manhattanInventoryJob = manhattanInventoryJob;
		this.filePath = filePath;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ftpUploadFile(new File(filePath));
		return RepeatStatus.FINISHED;
	}

	@Autowired
	public void setManhattanOutboundGateway(ManhattanOutboundGateway manhattanOutboundGateway) {
		this.manhattanOutboundGateway = manhattanOutboundGateway;
	}

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
	private void ftpUploadFile(File file) {
		log.debug("Started Uploading manhattan " + manhattanInventoryJob.getManhattanChannel().getStringValue() + " :" + file.getName());
		manhattanOutboundGateway.sendManhattanSupplyFileSftp(file);
		log.debug("Uploaded file");
		FileUtils.deleteQuietly(file);
	}
}
