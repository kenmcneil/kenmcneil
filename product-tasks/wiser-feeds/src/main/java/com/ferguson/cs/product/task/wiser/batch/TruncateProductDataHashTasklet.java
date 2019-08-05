package com.ferguson.cs.product.task.wiser.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.service.WiserService;

public class TruncateProductDataHashTasklet implements Tasklet{
	private WiserService wiserService;

	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}


	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		wiserService.truncateProductDataHashes();
		return RepeatStatus.FINISHED;
	}
}
