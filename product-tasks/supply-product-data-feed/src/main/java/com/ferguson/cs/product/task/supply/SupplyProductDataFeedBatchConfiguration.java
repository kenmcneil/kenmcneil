package com.ferguson.cs.product.task.supply;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.supply.model.Product;
import com.ferguson.cs.product.task.supply.service.SupplyProductDataFeedService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class SupplyProductDataFeedBatchConfiguration {
	private TaskBatchJobFactory taskBatchJobFactory;
	private SqlSessionFactory reporterSqlSessionFactory;
	private SupplyProductDataFeedService supplyProductDataFeedService;
	private SupplyProductDataFeedSettings supplyProductDataFeedSettings;

	private static final String DELIMITER = ",";

	@Autowired
	public void setTaskBatchJobFactory(TaskBatchJobFactory taskBatchJobFactory) {
		this.taskBatchJobFactory = taskBatchJobFactory;
	}

	@Autowired
	public void setReporterSqlSessionFactory(SqlSessionFactory reporterSqlSessionFactory) {
		this.reporterSqlSessionFactory = reporterSqlSessionFactory;
	}

	@Autowired
	public void setSupplyProductDataFeedService(SupplyProductDataFeedService supplyProductDataFeedService) {
		this.supplyProductDataFeedService = supplyProductDataFeedService;
	}

	@Autowired
	public void setSupplyProductDataFeedSettings(SupplyProductDataFeedSettings supplyProductDataFeedSettings) {
		this.supplyProductDataFeedSettings = supplyProductDataFeedSettings;
	}

	private MyBatisCursorItemReader dataReader (String jobName,String queryId){
		MyBatisCursorItemReader reader = new MyBatisCursorItemReader<>();

		reader.setSqlSessionFactory(reporterSqlSessionFactory);
		reader.setQueryId(queryId);
		Date lastRanDate =supplyProductDataFeedService.getLastRanDate(jobName);
		Map<String,Object> params = new HashMap<>();
		params.put("lastRanDate",lastRanDate);
		reader.setParameterValues(params);
		return reader;
	}

	@Bean
	public Job supplyProductDataFeedJob() {
		String jobName = "supplyProductDataFeedJob";
		return taskBatchJobFactory.getJobBuilder(jobName)
				.start(writeProductData(jobName))
				.next(writeVendorData(jobName))
				.next(uploadSupplyProductData())
				.build();
	}

	@Bean
	public Tasklet fileUploadTasklet() {
		return new FileUploadTasklet();
	}

	@Bean
	public Step uploadSupplyProductData() {
		return taskBatchJobFactory.getStepBuilder("uploadSupplyProductData")
				.tasklet(fileUploadTasklet())
				.build();
	}




	private Step writeProductData(String jobName) {

		String[] headerColumns = new String[] {"uniqueId","productId","manufacturer","finish","sku","upc","mpn","msrp","isLtl","freightCost","type","application","handleType","status"};
		String filePrefix = supplyProductDataFeedSettings.getFilePath() + "cs_product_data_";
		FieldExtractor fieldExtractor = new ProductFieldExtractor();



		return taskBatchJobFactory.getStepBuilder("writeProductData")
				.<Product,Product>chunk(1000)
				.faultTolerant()
				.reader(dataReader(jobName,"getProductData"))
				.writer(createItemWriter(headerColumns,filePrefix,fieldExtractor))
				.build();
	}

	private Step writeVendorData(String jobName) {

		String[] headerColumns = new String[] {"id","vendorName","vendorId","vendorAddress","vendorCity","vendorState","vendorZipCode","contactPhone","contactFax","lastUpdated","active"};
		String filePrefix = supplyProductDataFeedSettings.getFilePath() + "cs_vendor_data_";
		FieldExtractor fieldExtractor = new VendorFieldExtractor();



		return taskBatchJobFactory.getStepBuilder("writeProductData")
				.<Product,Product>chunk(1000)
				.faultTolerant()
				.reader(dataReader(jobName,"getVendorData"))
				.writer(createItemWriter(headerColumns,filePrefix,fieldExtractor))
				.build();
	}

	private ItemStreamWriter createItemWriter(String[] headerColumns, String filePrefix, FieldExtractor fieldExtractor) {
		FlatFileItemWriter productItemWriter = new FlatFileItemWriter<>();
		DelimitedLineAggregator productLineAggregator = new DelimitedLineAggregator<>();
		productLineAggregator.setFieldExtractor(fieldExtractor);

		CsvHeaderWriterCallback csvHeaderWriterCallback = new CsvHeaderWriterCallback(headerColumns,DELIMITER);

		productItemWriter.setLineAggregator(productLineAggregator);
		productItemWriter.setHeaderCallback(csvHeaderWriterCallback);
		LocalDate today = LocalDate.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String filePath = filePrefix + today.format(dateTimeFormatter) + ".csv";
		productItemWriter.setResource(new FileSystemResource(filePath));

		return productItemWriter;
	}

}
