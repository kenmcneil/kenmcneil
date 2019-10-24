package com.ferguson.cs.product.task.dy.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.dy.DyFeedConfiguration;
import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.ferguson.cs.product.task.dy.domain.ResourceObject;

public class UploadFileTasklet implements Tasklet {

	private DyFeedConfiguration.DynamicYieldGateway dyGateway;
	private DyFeedSettings dyFeedSettings;
	private ResourceObject resource;

	@Autowired
	public void setResourceObject(ResourceObject resource) {
		this.resource = resource;
	}

	@Autowired
	public void setWiserGateway(DyFeedConfiguration.DynamicYieldGateway dyGateway) {
		this.dyGateway = dyGateway;
	}

	@Autowired
	public void setDyFeedSettings(DyFeedSettings dyFeedSettings) {
		this.dyFeedSettings = dyFeedSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		uploadFile(resource.getResource().getPath());
		return null;
	}

	private void uploadFile(String filePath) {
		File file = new File(filePath);
		dyGateway.sendDyFileSftp(file);
		FileUtils.deleteQuietly(file);
	}
}
