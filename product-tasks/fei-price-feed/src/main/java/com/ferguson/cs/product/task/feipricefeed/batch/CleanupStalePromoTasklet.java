package com.ferguson.cs.product.task.feipricefeed.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;

public class CleanupStalePromoTasklet implements Tasklet {

	private final FeiPriceService feiPriceService;

	public CleanupStalePromoTasklet(FeiPriceService feiPriceService) {
		this.feiPriceService = feiPriceService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		feiPriceService.deleteStalePromoFeiPriceData();
		return RepeatStatus.FINISHED;
	}
}
