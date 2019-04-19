package com.ferguson.cs.product.task.inventory;

import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import com.ferguson.cs.product.task.inventory.batch.ManhattanJobInitializationTasklet;
import com.ferguson.cs.product.task.inventory.batch.ManhattanVendorInventoryJobListener;
import com.ferguson.cs.product.task.inventory.batch.ManhattanVendorInventoryProcessor;
import com.ferguson.cs.product.task.inventory.batch.ManhattanZeroesDecider;
import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class ManhattanInboundProcessorTaskConfiguration {

	private ManhattanInboundSettings manhattanInboundSettings;
	private TaskBatchJobFactory taskBatchJobFactory;
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
	@Qualifier("coreSqlSessionFactory")
	public void setCoreSqlSessionFactory(SqlSessionFactory coreSqlSessionFactory) {
		this.coreSqlSessionFactory = coreSqlSessionFactory;
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<VendorInventory> vendorInventoryFlatFileItemWriter(ManhattanIntakeJob manhattanIntakeJob) {


		return createVendorInventoryWriter(manhattanIntakeJob.getTransactionNumber());
	}

	@Bean
	public ManhattanVendorInventoryProcessor manhattanVendorInventoryProcessor() {
		return new ManhattanVendorInventoryProcessor();
	}

	@Bean
	public Step writeManhattanVendorInventory() {
		return taskBatchJobFactory.getStepBuilder("writeManhattanVendorInventory")
				.<VendorInventory, VendorInventory>chunk(1000)
				.reader(manhattanVendorInventoryReader(null))
				.writer(vendorInventoryFlatFileItemWriter(null))
				.build();
	}

	@Bean
	public Step writeManhattanVendorInventoryZeroes() {
		return taskBatchJobFactory.getStepBuilder("writeManhattanVendorInventoryZeroes")
				.<VendorInventory, VendorInventory>chunk(1000)
				.reader(manhattanVendorInventoryZeroesReader(null))
				.writer(vendorInventoryFlatFileItemWriter(null))
				.build();
	}

	@Bean
	public Step initializeManhattanJob() {
		return taskBatchJobFactory.getStepBuilder("initializeManhattanJob")
				.tasklet(manhattanJobInitializationTasklet())
				.build();
	}


	@Bean
	public Job manhattanInboundProcessorJob() {
		return taskBatchJobFactory.getJobBuilder("manhattanInboundProcessorJob")
				.listener(manhattanVendorInventoryJobListener())
				.start(initializeManhattanJob())
				.on(ExitStatus.NOOP.getExitCode()).end()
				.from(initializeManhattanJob()).on(ExitStatus.COMPLETED.getExitCode())
				.to(writeManhattanVendorInventory())
				.next(manhattanZeroesDecider())
				.on(ExitStatus.COMPLETED.getExitCode())
				.end()
				.on("ZEROES")
				.to(writeManhattanVendorInventoryZeroes())
				.end()
				.build();
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<VendorInventory> manhattanVendorInventoryReader(ManhattanIntakeJob manhattanIntakeJob) {
		return createVendorInventoryReader("getManhattanVendorInventory", manhattanIntakeJob.getTransactionNumber());
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<VendorInventory> manhattanVendorInventoryZeroesReader(ManhattanIntakeJob manhattanIntakeJob) {
		return createVendorInventoryReader("getManhattanVendorInventoryZeroes", manhattanIntakeJob.getTransactionNumber());
	}


	@Bean
	public ManhattanVendorInventoryJobListener manhattanVendorInventoryJobListener() {
		return new ManhattanVendorInventoryJobListener();
	}

	@Bean
	public ManhattanZeroesDecider manhattanZeroesDecider() {
		return new ManhattanZeroesDecider();
	}

	@Bean
	public ManhattanIntakeJob manhattanIntakeJob() {
		return new ManhattanIntakeJob();
	}

	@Bean
	public ManhattanJobInitializationTasklet manhattanJobInitializationTasklet() {
		return new ManhattanJobInitializationTasklet();
	}

	private MyBatisCursorItemReader<VendorInventory> createVendorInventoryReader(String queryName, String transactionNumber) {
		MyBatisCursorItemReader<VendorInventory> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId(queryName);
		reader.setSqlSessionFactory(coreSqlSessionFactory);

		Map<String, Object> params = new HashMap<>();
		params.put("transactionNumber", transactionNumber);

		reader.setParameterValues(params);
		return reader;


	}

	private FlatFileItemWriter<VendorInventory> createVendorInventoryWriter(String transactionNumber) {
		return new FlatFileItemWriterBuilder<VendorInventory>()
				.delimited()
				.delimiter(",")
				.names(new String[]{"sku", "location", "quantity"})
				.name("vendorInventoryFlatFileItemWriter")
				.resource(new FileSystemResource(manhattanInboundSettings.getManhattanOutputFilePath() + "_" + transactionNumber + ".csv"))
				.append(true)
				.build();
	}
}
