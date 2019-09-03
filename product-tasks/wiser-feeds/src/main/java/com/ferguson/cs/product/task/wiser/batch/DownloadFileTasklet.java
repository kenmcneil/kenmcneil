package com.ferguson.cs.product.task.wiser.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.ThreeSixtyPiSettings;
import com.ferguson.cs.product.task.wiser.WiserFeedConfiguration;
import com.ferguson.cs.product.task.wiser.model.FileDownloadRequest;

public class DownloadFileTasklet implements Tasklet {

	private WiserFeedConfiguration.WiserGateway wiserGateway;
	private ThreeSixtyPiSettings threeSixtyPiSettings;

	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileTasklet.class);


	@Autowired
	public void setWiserGateway(WiserFeedConfiguration.WiserGateway wiserGateway) {
		this.wiserGateway = wiserGateway;
	}

	@Autowired
	public void setThreeSixtyPiSettings(ThreeSixtyPiSettings threeSixtyPiSettings) {
		this.threeSixtyPiSettings = threeSixtyPiSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		String localFileName = jobExecutionContext.getString("fileName");
		String remoteFileName = jobExecutionContext.getString("remoteFileName");
		FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();

		fileDownloadRequest.setRemoteFilePath(threeSixtyPiSettings.getFtpFolder() + remoteFileName);
		fileDownloadRequest.setLocalFilePath(localFileName);

		downloadFile(fileDownloadRequest);

		return RepeatStatus.FINISHED;
	}


	private void downloadFile(FileDownloadRequest fileDownloadRequest) {

		LOG.debug("Started downloading file: {} - To local file: {}",fileDownloadRequest.getRemoteFilePath(),fileDownloadRequest.getLocalFilePath());
		wiserGateway.receive360piFileSftp(fileDownloadRequest);
		LOG.debug("Downloaded file");
		LOG.debug("Cleaning up remote file");
		wiserGateway.deleteWiserFileSftp(fileDownloadRequest.getRemoteFilePath());
		LOG.debug("Cleaned up remote file");
	}
}
