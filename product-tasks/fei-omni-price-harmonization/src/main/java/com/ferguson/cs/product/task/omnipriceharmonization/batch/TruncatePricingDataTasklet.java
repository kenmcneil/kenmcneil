package com.ferguson.cs.product.task.omnipriceharmonization.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.omnipriceharmonization.service.OmniPriceHarmonizationService;

public class TruncatePricingDataTasklet implements Tasklet {

	private final OmniPriceHarmonizationService omniPriceHarmonizationService;

	public TruncatePricingDataTasklet(OmniPriceHarmonizationService omniPriceHarmonizationService) {
		this.omniPriceHarmonizationService = omniPriceHarmonizationService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		omniPriceHarmonizationService.truncatePriceHarmonizationData();
		return RepeatStatus.FINISHED;
	}
}
