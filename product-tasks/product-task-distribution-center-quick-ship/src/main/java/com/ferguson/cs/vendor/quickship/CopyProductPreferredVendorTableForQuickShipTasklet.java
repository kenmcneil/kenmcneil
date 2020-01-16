package com.ferguson.cs.vendor.quickship;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.vendor.quickship.service.product.ProductService;

public class CopyProductPreferredVendorTableForQuickShipTasklet implements Tasklet {
	private final ProductService productService;

	public CopyProductPreferredVendorTableForQuickShipTasklet(ProductService productService) {
		this.productService = productService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		productService.populateProductPreferredVendorQuickShip();

		return RepeatStatus.FINISHED;
	}
}
