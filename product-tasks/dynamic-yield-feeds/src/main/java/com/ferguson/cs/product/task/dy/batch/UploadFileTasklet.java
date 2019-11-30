package com.ferguson.cs.product.task.dy.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.ferguson.cs.product.task.dy.domain.Sites;
import com.ferguson.cs.product.task.dy.service.DyAsyncService;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class UploadFileTasklet implements Tasklet {
	private final DyFeedSettings dyFeedSettings;
	private DyAsyncService dyAsyncService;

	@Qualifier("dyProductFileResource") Map<Integer, Resource> dyProductFileResource;
	private final Map<Integer, Resource> resources;

	public UploadFileTasklet(DyFeedSettings dyFeedSettings, Map<Integer, Resource> resources, DyAsyncService dyAsyncService) {
		this.dyFeedSettings = dyFeedSettings;
		this.resources = resources;
		this.dyAsyncService = dyAsyncService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws SftpException, JSchException, ExecutionException, InterruptedException {
		List<Future<Boolean>> ftpThreadsFinished = new ArrayList<Future<Boolean>>();

		for(Sites site : Sites.values()){
			ftpThreadsFinished.add(dyAsyncService.sendSftpFile(site.getSiteId(), dyFeedSettings, resources));
		}

		// Wait for all files to be sent before marking the task as finished
		for (Future<Boolean> ftpCompleted : ftpThreadsFinished) {
			while (!ftpCompleted.isDone()) {
				continue;
			}
		}

		return RepeatStatus.FINISHED;
	}

}
