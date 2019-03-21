package com.ferguson.cs.product.task.inventory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.inventory.batch.ManhattanJsonItemReader;
import com.ferguson.cs.product.task.inventory.batch.ManhattanVendorInventoryProcessor;
import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.LocationAvailabilityResponse;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryData;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class ManhattanInboundProcessorTaskConfiguration {

	private ManhattanInboundSettings manhattanInboundSettings;
	private TaskBatchJobFactory taskBatchJobFactory;

	@Autowired
	public void setManhattanInboundSettings(ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@Autowired
	public void setTaskBatchJobFactory(TaskBatchJobFactory taskBatchJobFactory) {
		this.taskBatchJobFactory = taskBatchJobFactory;
	}
	@Bean
	@StepScope
	public ManhattanJsonItemReader manhattanJsonItemReader() {

		return new ManhattanJsonItemReader();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<VendorInventory> vendorInventoryFlatFileItemWriter() {

		return new FlatFileItemWriterBuilder<VendorInventory>()
				.delimited()
				.delimiter(",")
				.names(new String[] {"mpn","location","status","quantity","eta"})
				.resource(new FileSystemResource(manhattanInboundSettings.getManhattanOutputFile()))
				.build();
	}

	@Bean
	public FileSystemResource fileSystemResource() {
		return new FileSystemResource(manhattanInboundSettings.getManhattanInputFile());
	}

	@Bean
	public ManhattanVendorInventoryProcessor manhattanVendorInventoryProcessor() {
		return new ManhattanVendorInventoryProcessor();
	}

	@Bean
	public Step writeManhattanVendorInventoryStep() {
		return taskBatchJobFactory.getStepBuilder("writeManhattanVendorInventoryStep")
				.<LocationAvailabilityResponse,VendorInventory>chunk(1000)
				.reader(manhattanJsonItemReader())
				.processor(manhattanVendorInventoryProcessor())
				.writer(vendorInventoryFlatFileItemWriter())
				.build();


	}

	@Bean
	public Job manhattanInboundProcessorJob() {
		return taskBatchJobFactory.getJobBuilder("manhattanInboundProcessorJob")
				.start(writeManhattanVendorInventoryStep())
				.build();
	}
}
