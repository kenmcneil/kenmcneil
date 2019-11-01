package com.ferguson.cs.product.task.dy.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.dy.DyFeedConfiguration;

public class UploadFileTasklet implements Tasklet {

	private final DyFeedConfiguration.DynamicYieldGateway dyGateway;

	@Qualifier("dyProductFileResource") File dyProductFileResource;
	private final FileSystemResource resource;

	public UploadFileTasklet(DyFeedConfiguration.DynamicYieldGateway dyGateway,
	                         FileSystemResource resource) {
		this.dyGateway = dyGateway;
		this.resource = resource;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		uploadFile(resource.getPath());
		return RepeatStatus.FINISHED;
	}

	private void uploadFile(String filePath) {
		File file = new File(filePath);
		dyGateway.sendDyFileSftp(file);
		FileUtils.deleteQuietly(file);
	}
}