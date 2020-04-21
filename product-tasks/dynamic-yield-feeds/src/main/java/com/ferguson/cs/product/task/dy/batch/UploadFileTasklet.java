package com.ferguson.cs.product.task.dy.batch;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.ferguson.cs.product.task.dy.domain.SiteProductFileResource;

public class UploadFileTasklet implements Tasklet {
	private final DyFeedSettings dyFeedSettings;
	private Integer siteId;
	private MessageChannel sftpChannel;
	private SiteProductFileResource dyProductFileResource;

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileTasklet.class);


	public UploadFileTasklet(Integer siteId, DyFeedSettings dyFeedSettings,
							 MessageChannel sftpChannel, SiteProductFileResource dyProductFileResource) {
		this.siteId = siteId;
		this.dyFeedSettings = dyFeedSettings;
		this.sftpChannel = sftpChannel;
		this.dyProductFileResource = dyProductFileResource;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {
		Resource fileResource = dyProductFileResource.getSiteFileMap().get(siteId);
		String userName = dyFeedSettings.getSiteUsername().get(siteId);

		if (StringUtils.hasText(userName) && fileResource != null) {
			String remoteDirectory = dyFeedSettings.getFtpRoot() + userName;

			sftpChannel.send(MessageBuilder.withPayload(fileResource.getFile())
					.setHeader("remoteDirectory", remoteDirectory)
					.setHeader("siteId", siteId)
					.setHeader("tempFile", fileResource.getFilename())
					.build());
		} else {
			LOGGER.error("No configuration or temp file found for siteId: " + siteId);
		}

		return RepeatStatus.FINISHED;
	}

}
