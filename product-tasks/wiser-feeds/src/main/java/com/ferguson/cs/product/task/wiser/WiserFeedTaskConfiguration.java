package com.ferguson.cs.product.task.wiser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.wiser.batch.ProductDataHashProcessor;
import com.ferguson.cs.product.task.wiser.batch.QuoteEnclosingDelimitedLineAggregator;
import com.ferguson.cs.product.task.wiser.batch.SetItemWriter;
import com.ferguson.cs.product.task.wiser.batch.TruncateProductDataHashTasklet;
import com.ferguson.cs.product.task.wiser.batch.UploadFileTasklet;
import com.ferguson.cs.product.task.wiser.batch.WiserFeedListener;
import com.ferguson.cs.product.task.wiser.batch.WiserProductDataProcessor;
import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.WiserFeedType;
import com.ferguson.cs.product.task.wiser.model.WiserProductData;
import com.ferguson.cs.product.task.wiser.service.WiserService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.utilities.DateUtils;

@Configuration
public class WiserFeedTaskConfiguration {
	private WiserService wiserService;
	private SqlSessionFactory coreSqlSessionFactory;
	private SqlSessionFactory integrationSqlSessionFactory;
	private SqlSessionFactory batchSqlSessionFactory;


	public static final String LOCAL_FILE_NAME_KEY = "localFileName";
	public static final String REMOTE_FILE_NAME_KEY = "remoteFileName";
	public static final String SFTP_SOURCE_KEY = "sftpSource";
	private TaskBatchJobFactory taskBatchJobFactory;

	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Autowired
	@Qualifier("coreSqlSessionFactory")
	public void setCoreSqlSessionFactory(SqlSessionFactory coreSqlSessionFactory) {
		this.coreSqlSessionFactory = coreSqlSessionFactory;
	}

	@Autowired
	@Qualifier("integrationSqlSessionFactory")
	public void setIntegrationSqlSessionFactory(SqlSessionFactory integrationSqlSessionFactory) {
		this.integrationSqlSessionFactory = integrationSqlSessionFactory;
	}

	@Autowired
	@Qualifier("batchSqlSessionFactory")
	public void setBatchSqlSessionFactory(SqlSessionFactory batchSqlSessionFactory) {
		this.batchSqlSessionFactory = batchSqlSessionFactory;
	}

	@Autowired
	public void setTaskBatchJobFactory(TaskBatchJobFactory taskBatchJobFactory) {
		this.taskBatchJobFactory = taskBatchJobFactory;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<ProductData> productDataReader() {
		MyBatisCursorItemReader<ProductData> productDataReader = new MyBatisCursorItemReader<>();
		productDataReader.setQueryId("getProductData");
		productDataReader.setSqlSessionFactory(coreSqlSessionFactory);
		return productDataReader;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<Integer> productDataHashReader(@Value("#{jobExecutionContext['jobName']}") String jobName) {
		Date date = wiserService.getLastRanDate(jobName);
		return getProductDataHashReader(date);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<Integer> fullProductDataHashReader() {
		return getProductDataHashReader(null);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<Integer> previousDayProductDataHashReader(@Value("#{jobExecutionContext['jobName']}") String jobName) {
		Date date = wiserService.getLastRanDate(jobName);
		date = DateUtils.addDaysToDate(date, -1);
		return getProductDataHashReader(date);
	}

	private MyBatisCursorItemReader<Integer> getProductDataHashReader(Date startDate) {
		MyBatisCursorItemReader<Integer> productDataHashReader = new MyBatisCursorItemReader<>();
		productDataHashReader.setQueryId("getProductDataHashUniqueIds");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("date", startDate);
		productDataHashReader.setParameterValues(parameters);
		productDataHashReader.setSqlSessionFactory(integrationSqlSessionFactory);
		return productDataHashReader;
	}

	@Bean
	@StepScope
	public ItemWriter<ProductDataHash> compositeProductDataHashWriter() {
		CompositeItemWriter<ProductDataHash> compositeItemWriter = new CompositeItemWriter<>();
		List<ItemWriter<? super ProductDataHash>> delegates = new ArrayList<>();
		delegates.add(productDataHashDeleteWriter());
		delegates.add(upsertProductDataHashWriter());
		compositeItemWriter.setDelegates(delegates);
		return compositeItemWriter;

	}

	@Bean
	@StepScope
	public MyBatisBatchItemWriter<ProductDataHash> upsertProductDataHashWriter() {
		MyBatisBatchItemWriter<ProductDataHash> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(batchSqlSessionFactory);
		writer.setStatementId("upsertProductDataHash");
		return writer;
	}

	@Bean
	@StepScope
	public MyBatisBatchItemWriter<ProductDataHash> insertProductDataHashWriter() {
		MyBatisBatchItemWriter<ProductDataHash> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(batchSqlSessionFactory);
		writer.setStatementId("insertProductDataHash");
		return writer;
	}

	@Bean
	@StepScope
	public MyBatisBatchItemWriter<ProductDataHash> productDataHashDeleteWriter() {
		MyBatisBatchItemWriter<ProductDataHash> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(batchSqlSessionFactory);
		writer.setStatementId("deleteProductDataHash");
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public ProductDataHashProcessor hashedProductDataProcessor() {
		return new ProductDataHashProcessor();
	}

	@Bean
	@StepScope
	public WiserProductDataProcessor wiserProductDataProcessor() {
		return new WiserProductDataProcessor();
	}

	@Bean
	@StepScope
	UploadFileTasklet uploadFileTasklet(@Value("#{jobExecutionContext['filePath']}") String filePath) {
		return new UploadFileTasklet(filePath);
	}

	@Bean
	@StepScope
	TruncateProductDataHashTasklet truncateProductDataHashTasklet() {
		return new TruncateProductDataHashTasklet();
	}

	@Bean
	@StepScope
	SetItemWriter<Integer> setItemWriter() {
		return new SetItemWriter<>();
	}

	@Bean
	@JobScope
	Set<Integer> getProductDataHashUniqueIds() {
		return new HashSet<>();
	}

	@Bean
	@StepScope
	ItemStreamWriter<WiserProductData> wiserCsvCatalogItemWriter(@Value("#{jobExecutionContext['filePath']}") String filePath) {
		String[] header = new String[]{"sku",
				"product_name",
				"product_url",
				"image_url",
				"brand",
				"upc",
				"mpn/model",
				"L1 category",
				"L2 category",
				"product_type",
				"on_map",
				"on_promo",
				"in_stock",
				"map_price",
				"product_price",
				"cost",
				"hct_category",
				"conversion_category"
		};

		BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
		extractor.setNames(new String[]{"sku",
				"productName",
				"productUrl",
				"imageUrl",
				"brand",
				"upc",
				"mpnModelNumber",
				"l1Category",
				"l2Category",
				"productType",
				"onMap",
				"onPromo",
				"inStock",
				"mapPrice",
				"productPrice",
				"cost",
				"hctCategory",
				"conversionCategory"});
		return getFlatFileItemWriter(header, filePath, extractor);
	}

	@Bean
	@JobScope
	public WiserFeedListener wiserProductCatalogFeedListener() {
		return new WiserFeedListener(WiserFeedType.PRODUCT_CATALOG_FEED);
	}

	/**
	 * Writes hashes for each product that has changed since the last run of this job. Changing ProductData or the
	 * underlying query that retrieves that that will result in the entire product database being written to this table.
	 * That will take roughly 25-30 minutes.
	 */
	@Bean
	@Qualifier("writeProductDataHashJob")
	public Job writeProductDataHashJob(Step upsertProductDataHash) {
		return taskBatchJobFactory.getJobBuilder("writeProductDataHashJob")
				.start(upsertProductDataHash)
				.build();
	}

	@Bean
	@Qualifier("repopulateProductDataHashJob")
	public Job repopulateProductDataHashJob(Step truncateProductDataHashes,Step insertProductDataHash) {
		return taskBatchJobFactory.getJobBuilder("repopulateProductDataHashJob")
				.start(truncateProductDataHashes)
				.next(insertProductDataHash)
				.build();
	}


	/**
	 * Writes product data matching all product hashes that have changed since the last time this job was run to a
	 * csv, uploads that csv to Wiser. If this has never been run, or if the related data is deleted, this will send
	 * the entire product catalog/all product data hashes.
	 */
	@Bean
	@Qualifier("productCatalogIncrementalUploadJob")
	public Job productCatalogIncrementalUploadJob(Step writeProductDataHashUniqueIds, Step writeWiserItems, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("productCatalogIncrementalUploadJob")
				.start(writeProductDataHashUniqueIds)
				.next(writeWiserItems)
				.next(uploadCsv)
				.listener(wiserProductCatalogFeedListener())
				.build();
	}

	/**
	 * Writes product data matching all product hashes, meaning the entire product catalog.
	 */
	@Bean
	@Qualifier("productCatalogFullUploadJob")
	public Job productCatalogFullUploadJob(Step writeAllProductDataHashUniqueIds, Step writeWiserItems, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("productCatalogFullUploadJob")
				.start(writeAllProductDataHashUniqueIds)
				.next(writeWiserItems)
				.next(uploadCsv)
				.listener(wiserProductCatalogFeedListener())
				.build();
	}

	/**
	 * Writes product data matching all product hashes that have changed since the day before the
	 * last time this job was run to a csv, uploads that csv to Wiser. If this has never been run, or if the related
	 * data is deleted, this will send the entire product catalog/all products related to data hashes.
	 */
	@Bean
	@Qualifier("productCatalogPreviousDayFeedJob")
	public Job productCatalogPreviousDayFeedJob(Step writePreviousDayProductDataHashUniqueIds, Step writeWiserItems, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("productCatalogPreviousDayFeedJob")
				.start(writePreviousDayProductDataHashUniqueIds)
				.next(writeWiserItems)
				.next(uploadCsv)
				.listener(wiserProductCatalogFeedListener())
				.build();
	}

	@Bean
	@Qualifier("insertProductDataHash")
	public Step insertProductDataHash() {

		return taskBatchJobFactory.getStepBuilder("writeProductDataHash")
				.<ProductData, ProductDataHash>chunk(1000)
				.faultTolerant()
				.reader(productDataReader())
				.processor(hashedProductDataProcessor())
				.writer(insertProductDataHashWriter())
				.allowStartIfComplete(true)
				.build();

	}

	@Bean
	@Qualifier("upsertProductDataHash")
	public Step upsertProductDataHash() {

		return taskBatchJobFactory.getStepBuilder("writeProductDataHash")
				.<ProductData, ProductDataHash>chunk(1000)
				.faultTolerant()
				.reader(productDataReader())
				.processor(hashedProductDataProcessor())
				.writer(upsertProductDataHashWriter())
				.allowStartIfComplete(true)
				.build();

	}

	@Bean
	public Step truncateProductDataHashes() {
		return taskBatchJobFactory.getStepBuilder("truncateProductDataHashes")
				.tasklet(truncateProductDataHashTasklet())
				.build();
	}

	@Bean
	public Step writeProductDataHashUniqueIds(MyBatisCursorItemReader<Integer> productDataHashReader) {


		return taskBatchJobFactory.getStepBuilder("writeProductDataHashUniqueIds")
				.<Integer, Integer>chunk(1000)
				.reader(productDataHashReader)
				.writer(setItemWriter())
				.build();


	}

	@Bean
	public Step writeAllProductDataHashUniqueIds() {
		return taskBatchJobFactory.getStepBuilder("writeAllProductDataHashUniqueIds")
				.<Integer, Integer>chunk(1000)
				.reader(fullProductDataHashReader())
				.writer(setItemWriter())
				.build();
	}

	@Bean
	public Step writePreviousDayProductDataHashUniqueIds(MyBatisCursorItemReader<Integer> previousDayProductDataHashReader) {
		return taskBatchJobFactory.getStepBuilder("writePreviousDayProductDataHashUniqueIds")
				.<Integer, Integer>chunk(1000)
				.reader(previousDayProductDataHashReader)
				.writer(setItemWriter())
				.build();
	}

	@Bean
	public Step writeWiserItems(ItemStreamWriter<WiserProductData> wiserCsvCatalogItemWriter) {

		return taskBatchJobFactory.getStepBuilder("writeWiserItems")
				.<ProductData, WiserProductData>chunk(1000)
				.reader(productDataReader())
				.processor(wiserProductDataProcessor())
				.writer(wiserCsvCatalogItemWriter)
				.build();

	}

	@Bean
	public Step uploadCsv(UploadFileTasklet uploadFileTasklet) {
		return taskBatchJobFactory.getStepBuilder("uploadCsv")
				.tasklet(uploadFileTasklet)
				.build();
	}


	private FlatFileItemWriter getFlatFileItemWriter(String[] fileHeader, String filePath, FieldExtractor fieldExtractor) {
		FlatFileItemWriter fileItemWriter = new FlatFileItemWriter<>();

		fileItemWriter.setHeaderCallback(writer -> writer.write(String.join(",", fileHeader)));

		fileItemWriter.setResource(new FileSystemResource(filePath));


		LineAggregator lineAggregator = createLineAggregator(fieldExtractor);
		fileItemWriter.setLineAggregator(lineAggregator);

		return fileItemWriter;
	}

	private LineAggregator createLineAggregator(FieldExtractor fieldExtractor) {
		QuoteEnclosingDelimitedLineAggregator lineAggregator = new QuoteEnclosingDelimitedLineAggregator<>();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(fieldExtractor);
		return lineAggregator;
	}

}
