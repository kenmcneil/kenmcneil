package com.ferguson.cs.product.task.wiser.batch;

import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.service.WiserService;

public class PopulateProductMetadataTasklet implements Tasklet {
	private Map<Integer, ProductRevenueCategory> productRevenueCategorization;
	private Map<Integer, ProductConversionBucket> productConversionBuckets;
	private WiserService wiserService;

	@Autowired
	public void setProductRevenueCategorization(Map<Integer,ProductRevenueCategory> productRevenueCategorization) {
		this.productRevenueCategorization = productRevenueCategorization;
	}

	@Autowired
	public void setProductConversionBucketsn(Map<Integer,ProductConversionBucket> productConversionBuckets) {
		this.productConversionBuckets = productConversionBuckets;
	}


	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		productRevenueCategorization.putAll(wiserService.getProductRevenueCategorization());
		productConversionBuckets.putAll(wiserService.getProductConversionBuckets());
		return RepeatStatus.FINISHED;
	}
}
