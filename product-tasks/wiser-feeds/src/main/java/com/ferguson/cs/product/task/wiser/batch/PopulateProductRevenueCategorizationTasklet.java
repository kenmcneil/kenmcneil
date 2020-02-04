package com.ferguson.cs.product.task.wiser.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.wiser.service.WiserService;

public class PopulateProductRevenueCategorizationTasklet implements Tasklet {

	private final WiserService wiserService;

	public PopulateProductRevenueCategorizationTasklet(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		wiserService.populateProductRevenueCategorization();
		return RepeatStatus.FINISHED;
	}
}
