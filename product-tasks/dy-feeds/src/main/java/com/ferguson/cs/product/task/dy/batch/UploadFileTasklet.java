package com.ferguson.cs.product.task.dy.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.dy.DyFeedConfiguration;
import com.ferguson.cs.product.task.dy.domain.ResourceObject;

public class UploadFileTasklet implements Tasklet {

	private final DyFeedConfiguration.DynamicYieldGateway dyGateway;
	private final ResourceObject resource;

	public UploadFileTasklet(DyFeedConfiguration.DynamicYieldGateway dyGateway,
	                         ResourceObject resource) {
		this.dyGateway = dyGateway;
		this.resource = resource;
	}
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		uploadFile(resource.getResource().getPath());
		return RepeatStatus.FINISHED;
	}

	private void uploadFile(String filePath) {
		File file = new File(filePath);
		dyGateway.sendDyFileSftp(file);
		FileUtils.deleteQuietly(file);
	}
}
