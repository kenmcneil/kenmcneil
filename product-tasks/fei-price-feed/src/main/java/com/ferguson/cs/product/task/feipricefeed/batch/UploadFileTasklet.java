package com.ferguson.cs.product.task.feipricefeed.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.feipricefeed.FeiPriceConfiguration;

public class UploadFileTasklet implements Tasklet {
	private final FeiFileSystemResource feiFileSystemResource;
	private final FeiPriceConfiguration.FeiGateway feiGateway;

	public UploadFileTasklet(FeiFileSystemResource feiFileSystemResource, FeiPriceConfiguration.FeiGateway feiGateway) {
		this.feiFileSystemResource = feiFileSystemResource;
		this.feiGateway = feiGateway;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(feiFileSystemResource != null && feiFileSystemResource.getFileSystemResource() != null) {
			uploadFile(feiFileSystemResource.getFileSystemResource());
		}
		return null;
	}

	private void uploadFile(FileSystemResource file) {
//		feiGateway.sendFeiFileSftp(file.getFile());
//		FileUtils.deleteQuietly(file.getFile());
	}
}
