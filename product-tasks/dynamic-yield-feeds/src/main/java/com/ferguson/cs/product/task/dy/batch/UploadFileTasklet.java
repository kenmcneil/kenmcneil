package com.ferguson.cs.product.task.dy.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.ferguson.cs.product.task.dy.service.DyService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class UploadFileTasklet implements Tasklet {
	private final DyFeedSettings dyFeedSettings;
	private DyService dyService;
	private String filename;
	private Integer siteId;


	public UploadFileTasklet(String filename, Integer siteId, DyFeedSettings dyFeedSettings, DyService dyService) {
		this.filename = filename;
		this.siteId = siteId;
		this.dyFeedSettings = dyFeedSettings;
		this.dyService = dyService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
			throws SftpException, JSchException {
			dyService.sendSftpFile(siteId, dyFeedSettings, filename);

		return RepeatStatus.FINISHED;
	}

}
