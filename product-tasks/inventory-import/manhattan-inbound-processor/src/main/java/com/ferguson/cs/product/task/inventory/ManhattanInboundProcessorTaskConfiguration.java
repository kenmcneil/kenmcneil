package com.ferguson.cs.product.task.inventory;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.inventory.batch.CreateManhattanTempTableTasklet;
import com.ferguson.cs.product.task.inventory.batch.ManhattanJsonItemReader;
import com.ferguson.cs.product.task.inventory.batch.ManhattanVendorInventoryProcessor;
import com.ferguson.cs.product.task.inventory.batch.ManhattanVendorInventoryJobListener;
import com.ferguson.cs.product.task.inventory.batch.ManhattanZeroesDecider;
import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.LocationAvailabilityResponse;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class ManhattanInboundProcessorTaskConfiguration {

	private ManhattanInboundSettings manhattanInboundSettings;
	private TaskBatchJobFactory taskBatchJobFactory;
	private SqlSessionFactory batchSqlSessionFactory;
	private SqlSessionFactory coreSqlSessionFactory;


	@Autowired
	public void setManhattanInboundSettings(ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@Autowired
	public void setTaskBatchJobFactory(TaskBatchJobFactory taskBatchJobFactory) {
		this.taskBatchJobFactory = taskBatchJobFactory;
	}

	@Autowired
	@Qualifier("batchSqlSessionFactory")
	public void setBatchSqlSessionFactory(SqlSessionFactory batchSqlSessionFactory) {
		this.batchSqlSessionFactory = batchSqlSessionFactory;
	}

	@Autowired
	public void setCoreSqlSessionFactory(SqlSessionFactory coreSqlSessionFactory) {
		this.coreSqlSessionFactory = coreSqlSessionFactory;
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
				.names(new String[] {"mpn","location","status","quantity"})
				.resource(new FileSystemResource(manhattanInboundSettings.getManhattanOutputFile()))
				.append(true)
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
	public Step writeManhattanVendorInventoryToTempTable() {
		return taskBatchJobFactory.getStepBuilder("writeManhattanVendorInventoryStep")
				.<LocationAvailabilityResponse,VendorInventory>chunk(1000)
				.reader(manhattanJsonItemReader())
				.processor(manhattanVendorInventoryProcessor())
				.writer(manhattanTempTableWriter())
				.build();


	}

	@Bean
	public Step createManhattanTempTable() {
		return taskBatchJobFactory.getStepBuilder("createManhattanTempTable")
				.tasklet(createManhattanTempTableTasklet())
				.build();
	}

	@Bean
	public Step writeManhattanVendorInventory() {
		return taskBatchJobFactory.getStepBuilder("writeManhattanVendorInventory")
				.<VendorInventory,VendorInventory>chunk(1000)
				.reader(manhattanVendorInventoryReader(null))
				.writer(vendorInventoryFlatFileItemWriter())
				.build();
	}

	@Bean
	public Step writeManhattanVendorInventoryZeroes() {
		return taskBatchJobFactory.getStepBuilder("writeManhattanVendorInventoryZeroes")
				.<VendorInventory,VendorInventory>chunk(1000)
				.reader(manhattanVendorInventoryZeroesReader(null))
				.writer(vendorInventoryFlatFileItemWriter())
				.build();
	}

	@Bean
	public Job manhattanInboundProcessorJob() {
		return taskBatchJobFactory.getJobBuilder("manhattanInboundProcessorJob")
				.listener(manhattanVendorInventoryJobListener())
				.start(createManhattanTempTable())
				.next(writeManhattanVendorInventoryToTempTable())
				.next(writeManhattanVendorInventory())
				.next(manhattanZeroesDecider())
				.on("ZEROES")
				.to(writeManhattanVendorInventoryZeroes())
				.end()
				.build();
	}

	@Bean
	@StepScope
	public MyBatisBatchItemWriter<VendorInventory> manhattanTempTableWriter() {
		MyBatisBatchItemWriter<VendorInventory> writer = new MyBatisBatchItemWriter<>();

		writer.setStatementId("insertManhattanVendorInventory");
		writer.setSqlSessionFactory(batchSqlSessionFactory);

		return writer;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<VendorInventory> manhattanVendorInventoryReader(@Value("#{jobExecutionContext['jobKey']}") String jobKey) {
		return createVendorInventoryReader("getManhattanVendorInventory",jobKey);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<VendorInventory> manhattanVendorInventoryZeroesReader(@Value("#{jobExecutionContext['jobKey']}") String jobKey) {
		return createVendorInventoryReader("getManhattanVendorInventoryZeroes",jobKey);
	}



	@Bean
	public ManhattanVendorInventoryJobListener manhattanVendorInventoryJobListener() {
		return new ManhattanVendorInventoryJobListener();
	}

	@Bean
	public CreateManhattanTempTableTasklet createManhattanTempTableTasklet() {
		return new CreateManhattanTempTableTasklet();
	}

	@Bean
	public ManhattanZeroesDecider manhattanZeroesDecider() {
		return new ManhattanZeroesDecider();
	}

	private MyBatisCursorItemReader<VendorInventory> createVendorInventoryReader(String queryName, String jobKey) {
		MyBatisCursorItemReader<VendorInventory> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId(queryName);

		Map<String, Object> params  = new HashMap<>();
		params.put("jobKey",jobKey);

		reader.setParameterValues(params);
		return reader;


	}
}
