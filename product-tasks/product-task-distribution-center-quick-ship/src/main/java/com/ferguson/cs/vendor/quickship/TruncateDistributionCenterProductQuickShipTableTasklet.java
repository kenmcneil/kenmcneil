package com.ferguson.cs.vendor.quickship;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.vendor.quickship.service.vendor.VendorService;

public class TruncateDistributionCenterProductQuickShipTableTasklet implements Tasklet {
	private final VendorService vendorService;

	public TruncateDistributionCenterProductQuickShipTableTasklet(VendorService vendorService) {
		this.vendorService = vendorService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		vendorService.truncateVendorProductQuickShipTable();

		return RepeatStatus.FINISHED;
	}
}
