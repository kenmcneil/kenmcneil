package com.ferguson.cs.product.task.wiser.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.WiserFeedConfiguration;

public class UploadFileTasklet implements Tasklet {

	private String filePath;
	private WiserFeedConfiguration.WiserGateway wiserGateway;

	public UploadFileTasklet(String filePath) {
		this.filePath = filePath;
	}

	@Autowired
	public void setWiserGateway(WiserFeedConfiguration.WiserGateway wiserGateway) {
		this.wiserGateway = wiserGateway;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(filePath != null) {
			uploadFile(filePath);
		}
		return null;
	}


	private void uploadFile(String filePath) {
		File file = new File(filePath);

		wiserGateway.sendWiserFileSftp(file);

		FileUtils.deleteQuietly(file);
	}
}
