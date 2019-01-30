package com.ferguson.cs.product.task.brand.ge;



import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.task.configuration.SimpleTaskConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.product.task.brand.ge.task.GeDeletInactiveProductsTasklet;
import com.ferguson.cs.product.task.brand.ge.task.GeDimensionsTasklet;
import com.ferguson.cs.product.task.brand.ge.task.GeProductProcessor;
import com.ferguson.cs.product.task.brand.ge.task.GeProductReader;
import com.ferguson.cs.product.task.brand.ge.task.GeProductWriter;
import com.ferguson.cs.product.task.brand.ge.task.GeProductsJobDecider;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.service.ProductDistributionService;
import com.ferguson.cs.product.task.brand.service.ProductDistributionServiceImpl;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ge_products.api.GeProduct;


@Configuration
public class GeProductsTasksConfiguration {

	@Autowired 
	private TaskBatchJobFactory taskBatchJobFactory;
	
	@Value("${numberOfProducts.read.page.size:20}")
	private int pageSize;
	
	@Bean
	public Step createGeProductsStep() {
		return taskBatchJobFactory.getStepBuilder("createGeProductsStep")
				.<GeProduct, BrandProduct> chunk(pageSize)
				.faultTolerant()
				.reader(geProductReader())
				.processor(geProductProcessor())
				.writer(geProductWriter())
				.allowStartIfComplete(true)
				.build();
	}
	
	
	@Bean
	@StepScope
	public ItemReader<GeProduct> geProductReader() {
		GeProductReader reader = new GeProductReader();
		reader.setPageSize(pageSize);
		return reader;
	}
	
	@Bean
	@StepScope
	public ItemProcessor<GeProduct,BrandProduct> geProductProcessor() {
		return new GeProductProcessor();
	}
	
	@Bean
	@StepScope
	public ItemWriter<BrandProduct> geProductWriter() {
		return new GeProductWriter();
	}

	 
	@Bean
	public GeProductsJobDecider geProductsJobDecider() {
		return new GeProductsJobDecider();
	}
	
	@Bean
	public Job processGeProducts() {
		
		 FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("flow1");

	        Flow flow = flowBuilder
	            .start(processGeProductsStatusUpdate())
	            .next(createGeProductsStep())
	            .next(geProductsJobDecider())
	            .on(GeProductsJobDecider.CONTINUE)
	            .to(createGeProductsStep())
	            .from(geProductsJobDecider())
	            .on(GeProductsJobDecider.COMPLETED)
	            .end()
	            .build();

	        return taskBatchJobFactory.getJobBuilder("processGeProducts")
	                .start(flow)
	                .next(processGeInactiveProducts())
	                .end()
	                .build();

	}

	
	@Bean
	public Step processGeProductsStatusUpdate() {
		return taskBatchJobFactory.getStepBuilder("processGeProductsStatusUpdate")
				.tasklet(updateGeProductsStatusTasklet()).build();
	}
	
	@Bean
	public GeDimensionsTasklet updateGeProductsStatusTasklet() {
		return new GeDimensionsTasklet();
	}

	@Bean
	public Step processGeInactiveProducts() {
		return taskBatchJobFactory.getStepBuilder("processGeInactiveProducts")
				.tasklet(deleteGeProductsTasklet()).build();
	}
	
	
	@Bean
	public GeDeletInactiveProductsTasklet deleteGeProductsTasklet() {
		return new GeDeletInactiveProductsTasklet();
	}
	
	@Bean
	public ProductDistributionService productDistributionService() {
		return new ProductDistributionServiceImpl();
	}
}
