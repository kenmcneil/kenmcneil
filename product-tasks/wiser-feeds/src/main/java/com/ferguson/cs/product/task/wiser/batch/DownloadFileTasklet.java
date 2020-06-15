package com.ferguson.cs.product.task.wiser.batch;

import java.io.File;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.wiser.model.FileDownloadRequest;

public class DownloadFileTasklet implements Tasklet {

	private Function<FileDownloadRequest, File> fileDownloadFunction;

	private static final Logger LOG = LoggerFactory.getLogger(DownloadFileTasklet.class);

	public DownloadFileTasklet(Function<FileDownloadRequest,File> fileDownloadFunction) {
		this.fileDownloadFunction = fileDownloadFunction;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		String localFileName = jobExecutionContext.getString("fileName");
		String remoteDownloadFilePath = jobExecutionContext.getString("remoteDownloadFilePath");
		FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();

		fileDownloadRequest.setRemoteFilePath(remoteDownloadFilePath);
		fileDownloadRequest.setLocalFilePath(localFileName);

		LOG.debug("Started downloading file: {} - To local file: {}",fileDownloadRequest.getRemoteFilePath(),fileDownloadRequest.getLocalFilePath());
		//fileDownloadFunction.apply(fileDownloadRequest);
		LOG.debug("Downloaded file");

		return RepeatStatus.FINISHED;
	}
}
