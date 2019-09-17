package com.ferguson.cs.vendor.quickship;

import java.io.IOException;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.retry.annotation.EnableRetry;

import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.service.category.CategoryService;
import com.ferguson.cs.vendor.quickship.service.product.ProductService;
import com.ferguson.cs.vendor.quickship.service.vendor.VendorService;

@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.vendor.quickship")
@EnableRetry
public class DistributionCenterQuickShipJobConfiguration {
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final DistributionCenterQuickShipTaskConfiguration taskConfiguration;
	private final VendorService vendorService;
	private final ProductService productService;
	private final CategoryService categoryService;

	public DistributionCenterQuickShipJobConfiguration(TaskBatchJobFactory taskBatchJobFactory,
			DistributionCenterQuickShipTaskConfiguration taskConfiguration, VendorService vendorService,
			ProductService productService, CategoryService categoryService) {
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.taskConfiguration = taskConfiguration;
		this.vendorService = vendorService;
		this.productService = productService;
		this.categoryService = categoryService;
	}

	/**
	 * Truncate distribution center product Quick Ship table
	 * @return
	 */
	@Bean
	public Step truncateDistributionCenterProductQuickShipTable() {
		return taskBatchJobFactory.getStepBuilder("truncateDistributionCenterProductQuickShipTable")
				.tasklet(truncateTasklet()).build();
	}

	@Bean
	@StepScope
	public TruncateDistributionCenterProductQuickShipTableTasklet truncateTasklet() {
		final TruncateDistributionCenterProductQuickShipTableTasklet tasklet =
				new TruncateDistributionCenterProductQuickShipTableTasklet(vendorService);

		return tasklet;
	}

	/**
	 * Populate Distribution Center Product Quick Ship Table
	 * @return
	 * @throws IOException
	 */
	@Bean
	public Step populateDistributionCenterProductQuickShipTable() {
		return taskBatchJobFactory.getStepBuilder("populateDistributionCenterProductQuickShipTable")
				//Chunk size set 1 since we are using manual query pagination
				.<List<Product>, List<DistributionCenterProductQuickShip>> chunk(1)
				.faultTolerant()
				.reader(quickShipEligibleProductItemReader())
				.processor(quickShipEligibleProductProcessor())
				.writer(distributionCenterProductQuickShipItemWriter())
				.allowStartIfComplete(true)
				.build();
	}

	@Bean
	@StepScope
	public ItemReader<List<Product>> quickShipEligibleProductItemReader() {
		return new QuickShipEligibleProductItemReader(productService);
	}

	@Bean
	@StepScope
	public ItemProcessor<List<Product>, List<DistributionCenterProductQuickShip>> quickShipEligibleProductProcessor() {
		return new QuickShipEligibleProductProcessor(productService, vendorService, categoryService);
	}

	@Bean
	@StepScope
	public ItemWriter<? super List<DistributionCenterProductQuickShip>> distributionCenterProductQuickShipItemWriter() {
		return new DistributionCenterProductQuickShipItemWriter(vendorService, productService);
	}

	@Bean
	public Job distributionCenterProductQuickShipJob() {
		return taskBatchJobFactory.getJobBuilder("distributionCenterProductQuickShipJob")
				.start(truncateDistributionCenterProductQuickShipTable())
				.next(populateDistributionCenterProductQuickShipTable())
				.build();
	}
}
