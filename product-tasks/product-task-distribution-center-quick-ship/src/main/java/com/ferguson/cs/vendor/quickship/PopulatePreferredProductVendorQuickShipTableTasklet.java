package com.ferguson.cs.vendor.quickship;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.vendor.quickship.service.product.ProductService;

public class PopulatePreferredProductVendorQuickShipTableTasklet implements Tasklet {
	private final ProductService productService;

	public PopulatePreferredProductVendorQuickShipTableTasklet(ProductService productService) {
		this.productService = productService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		productService.truncateProductPreferredVendorQuickShip();
		productService.populateProductPreferredVendorQuickShip();
		return RepeatStatus.FINISHED;
	}
}
