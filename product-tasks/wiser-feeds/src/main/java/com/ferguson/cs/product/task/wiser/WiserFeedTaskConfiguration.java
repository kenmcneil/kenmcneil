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

import com.ferguson.cs.product.task.wiser.batch.DownloadFileTasklet;
import com.ferguson.cs.product.task.wiser.batch.FlatteningItemStreamWriter;
import com.ferguson.cs.product.task.wiser.batch.PopulateProductMetadataTasklet;
import com.ferguson.cs.product.task.wiser.batch.ProductDataHashProcessor;
import com.ferguson.cs.product.task.wiser.batch.QuoteEnclosingDelimitedLineAggregator;
import com.ferguson.cs.product.task.wiser.batch.SetItemWriter;
import com.ferguson.cs.product.task.wiser.batch.TruncateProductDataHashTasklet;
import com.ferguson.cs.product.task.wiser.batch.UploadFileTasklet;
import com.ferguson.cs.product.task.wiser.batch.WiserFeedListener;
import com.ferguson.cs.product.task.wiser.batch.WiserPerformanceDataProcessor;
import com.ferguson.cs.product.task.wiser.batch.WiserPriceDataProcessor;
import com.ferguson.cs.product.task.wiser.batch.WiserProductDataProcessor;
import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserFeedType;
import com.ferguson.cs.product.task.wiser.model.WiserPerformanceData;
import com.ferguson.cs.product.task.wiser.model.WiserPriceData;
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
	private WiserFeedSettings wiserFeedSettings;

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

	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
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
	public MyBatisCursorItemReader<WiserPriceData> wiserIncrementalPriceDataReader(@Value("#{jobExecutionContext['jobName']}") String jobName) {
		Date date = wiserService.getLastRanDate(jobName);
		return getWiserPriceDataReader(date);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<WiserPriceData> wiserFullPriceDataReader() {
		return getWiserPriceDataReader(null);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<Integer> previousDayProductDataHashReader(@Value("#{jobExecutionContext['jobName']}") String jobName) {
		Date date = wiserService.getLastRanDate(jobName);
		date = DateUtils.addDaysToDate(date, -1);
		return getProductDataHashReader(date);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<WiserPerformanceData> wiserPerformanceDataReader(@Value("#{jobExecutionContext['jobName']}") String jobName) {
		Date date = wiserService.getLastRanDate(jobName);
		if(date == null) {
			date = DateUtils.addDaysToDate(new Date(),-1);
		}
		MyBatisCursorItemReader<WiserPerformanceData> wiserPerformanceDataReader = new MyBatisCursorItemReader<>();
		wiserPerformanceDataReader.setQueryId("getWiserPerformanceData");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("date",date);
		wiserPerformanceDataReader.setParameterValues(parameters);
		wiserPerformanceDataReader.setSqlSessionFactory(coreSqlSessionFactory);
		return wiserPerformanceDataReader;
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

	private MyBatisCursorItemReader<WiserPriceData> getWiserPriceDataReader(Date date) {
		MyBatisCursorItemReader<WiserPriceData> wiserPriceDataReader = new MyBatisCursorItemReader<>();
		wiserPriceDataReader.setQueryId("getWiserPriceData");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("date", date);
		wiserPriceDataReader.setParameterValues(parameters);
		wiserPriceDataReader.setSqlSessionFactory(coreSqlSessionFactory);
		return wiserPriceDataReader;
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
	public WiserPriceDataProcessor wiserPriceDataProcessor() {
		return new WiserPriceDataProcessor();
	}

	@Bean
	@StepScope
	public WiserPerformanceDataProcessor wiserPerformanceDataProcessor() {
		return new WiserPerformanceDataProcessor();
	}

	@Bean
	@StepScope
	UploadFileTasklet uploadFileTasklet(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		return new UploadFileTasklet(wiserFeedSettings.getLocalFilePath() + fileName);
	}


	@Bean
	@StepScope
	DownloadFileTasklet downloadFileTasklet() {
		return new DownloadFileTasklet();
	}

	@Bean
	@StepScope
	TruncateProductDataHashTasklet truncateProductDataHashTasklet() {
		return new TruncateProductDataHashTasklet();
	}

	@Bean
	@StepScope
	PopulateProductMetadataTasklet populateProductRevenueCategorizationMapTasklet() {
		return new PopulateProductMetadataTasklet();
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
	@JobScope
	Map<Integer, ProductRevenueCategory> getProductRevenueCategorization() {
		return new HashMap<>();
	}

	@Bean
	@JobScope
	Map<Integer, ProductConversionBucket> getProductConversionBuckets() {
		return new HashMap<>();
	}

	@Bean
	@StepScope
	ItemStreamWriter<WiserProductData> wiserCsvCatalogItemWriter(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		String[] header = new String[]{"sku",
				"product_name",
				"product_url",
				"image_url",
				"brand",
				"upc",
				"mpn/model",
				"L1 category",
				"L2 category",
				"application",
				"product_type",
				"on_map",
				"map_price",
				"on_promo",
				"in_stock",
				"product_price",
				"cost",
				"list_price",
				"hct_category",
				"conversion_category",
				"is_ltl",
				"sale_id",
				"date_added",

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
				"application",
				"productType",
				"onMap",
				"mapPrice",
				"onPromo",
				"inStock",
				"productPrice",
				"cost",
				"listPrice",
				"hctCategory",
				"conversionCategory",
				"isLtl",
				"saleId",
				"dateAdded"

		});
		return getFlatFileItemWriter(header, wiserFeedSettings.getLocalFilePath() + fileName, extractor);
	}

	@Bean
	@StepScope
	ItemStreamWriter<List<WiserPriceData>> wiserCsvPriceDataItemWriter(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		String[] header = new String[]{"sku",
				"channel",
				"effective_date",
				"reg_price"};

		BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
		extractor.setNames(new String[]{"sku",
				"channel",
				"effectiveDate",
				"regularPrice"});

		ItemStreamWriter<WiserPriceData> innerWriter = getFlatFileItemWriter(header, wiserFeedSettings.getLocalFilePath() + fileName, extractor);

		return new FlatteningItemStreamWriter<>(innerWriter);
	}

	@Bean
	@StepScope
	ItemStreamWriter<WiserPerformanceData> wiserCsvPerformanceDataItemWriter(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		String[] header = new String[] { "sku",
				"transaction_date",
				"gross_units",
				"gross_orders",
				"gross_revenue",
				"channel",
				"ncr",
				"marketplace_id"};

		BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
		extractor.setNames(new String[] {"sku",
				"transactionDate",
				"grossUnits",
				"grossOrders",
				"grossRevenue",
				"channel",
				"ncr",
				"marketplaceId"});

		return getFlatFileItemWriter(header,wiserFeedSettings.getLocalFilePath() + fileName,extractor);
	}

	@Bean
	@JobScope
	public WiserFeedListener wiserProductCatalogFeedListener() {
		return new WiserFeedListener(WiserFeedType.PRODUCT_CATALOG_FEED);
	}

	@Bean
	@JobScope
	public WiserFeedListener wiserPriceFeedListener() {
		return new WiserFeedListener(WiserFeedType.PRICE_FEED);
	}

	@Bean
	@JobScope
	public WiserFeedListener wiserCompetitorFeedListener() {
		return new WiserFeedListener(WiserFeedType.COMPETITOR_FEED);
	}

	@Bean
	@JobScope
	public WiserFeedListener wiserPerformanceFeedListener() {
		return new WiserFeedListener(WiserFeedType.PERFORMANCE_FEED);
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
				.start(populateProductMetadata())
				.next(upsertProductDataHash)
				.build();
	}

	@Bean
	@Qualifier("repopulateProductDataHashJob")
	public Job repopulateProductDataHashJob(Step truncateProductDataHashes, Step insertProductDataHash) {
		return taskBatchJobFactory.getJobBuilder("repopulateProductDataHashJob")
				.start(truncateProductDataHashes)
				.next(populateProductMetadata())
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
				.start(populateProductMetadata())
				.next(writeProductDataHashUniqueIds)
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
	public Job productCatalogFullUploadJob(Step writeWiserItems, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("productCatalogFullUploadJob")
				.start(populateProductMetadata())
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
				.start(populateProductMetadata())
				.next(writePreviousDayProductDataHashUniqueIds)
				.next(writeWiserItems)
				.next(uploadCsv)
				.listener(wiserProductCatalogFeedListener())
				.build();
	}

	@Bean
	@Qualifier("priceDataIncrementalUploadJob")
	public Job priceDataIncrementalUploadJob(Step writeWiserPriceData, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("priceDataIncrementalUploadJob")
				.start(writeWiserPriceData)
				.next(uploadCsv)
				.listener(wiserPriceFeedListener())
				.build();
	}

	@Bean
	@Qualifier("priceDataFullUploadJob")
	public Job priceDataFullUploadJob(Step writeFullWiserPriceData, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("priceDataFullUploadJob")
				.start(writeFullWiserPriceData)
				.next(uploadCsv)
				.listener(wiserPriceFeedListener())
				.build();
	}

	@Bean
	@Qualifier("competitorDataFeedJob")
	public Job competitorDataFeedJob(Step downloadCsv, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("competitorDataFeedJob")
				.start(downloadCsv)
				.next(uploadCsv)
				.listener(wiserCompetitorFeedListener())
				.build();
	}

	@Bean
	@Qualifier("performanceDataUploadJob")
	public Job performanceDataUploadJob(Step writeWiserPerformanceData, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("performanceDataUploadJob")
				.start(writeWiserPerformanceData)
				.next(uploadCsv)
				.listener(wiserPerformanceFeedListener())
				.build();
	}

	@Bean
	@Qualifier("insertProductDataHash")
	public Step insertProductDataHash() {

		return taskBatchJobFactory.getStepBuilder("insertProductDataHash")
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

		return taskBatchJobFactory.getStepBuilder("upsertProductDataHash")
				.<ProductData, ProductDataHash>chunk(1000)
				.faultTolerant()
				.reader(productDataReader())
				.processor(hashedProductDataProcessor())
				.writer(upsertProductDataHashWriter())
				.allowStartIfComplete(true)
				.build();

	}

	@Bean
	@Qualifier("truncateProductDataHashes")
	public Step truncateProductDataHashes() {
		return taskBatchJobFactory.getStepBuilder("truncateProductDataHashes")
				.tasklet(truncateProductDataHashTasklet())
				.build();
	}

	@Bean
	@Qualifier("populateProductMetadata")
	public Step populateProductMetadata() {
		return taskBatchJobFactory.getStepBuilder("populateProductMetadata")
				.tasklet(populateProductRevenueCategorizationMapTasklet())
				.build();
	}

	@Bean
	@Qualifier("writeProductDataHashUniqueIds")
	public Step writeProductDataHashUniqueIds(MyBatisCursorItemReader<Integer> productDataHashReader) {


		return taskBatchJobFactory.getStepBuilder("writeProductDataHashUniqueIds")
				.<Integer, Integer>chunk(1000)
				.reader(productDataHashReader)
				.writer(setItemWriter())
				.build();


	}

	@Bean
	@Qualifier("writeAllProductDataHashUniqueIds")
	public Step writeAllProductDataHashUniqueIds() {
		return taskBatchJobFactory.getStepBuilder("writeAllProductDataHashUniqueIds")
				.<Integer, Integer>chunk(1000)
				.reader(fullProductDataHashReader())
				.writer(setItemWriter())
				.build();
	}

	@Bean
	@Qualifier("writePreviousDayProductDataHashUniqueIds")
	public Step writePreviousDayProductDataHashUniqueIds(MyBatisCursorItemReader<Integer> previousDayProductDataHashReader) {
		return taskBatchJobFactory.getStepBuilder("writePreviousDayProductDataHashUniqueIds")
				.<Integer, Integer>chunk(1000)
				.reader(previousDayProductDataHashReader)
				.writer(setItemWriter())
				.build();
	}

	@Bean
	@Qualifier("writeWiserItems")
	public Step writeWiserItems(ItemStreamWriter<WiserProductData> wiserCsvCatalogItemWriter) {

		return taskBatchJobFactory.getStepBuilder("writeWiserItems")
				.<ProductData, WiserProductData>chunk(1000)
				.reader(productDataReader())
				.processor(wiserProductDataProcessor())
				.writer(wiserCsvCatalogItemWriter)
				.build();

	}

	@Bean
	@Qualifier("writeWiserPriceData")
	public Step writeWiserPriceData(MyBatisCursorItemReader<WiserPriceData> wiserIncrementalPriceDataReader, ItemStreamWriter<List<WiserPriceData>> wiserCsvPriceDataItemWriter) {
		return taskBatchJobFactory.getStepBuilder("writeWiserPriceData")
				.<WiserPriceData, List<WiserPriceData>>chunk(1000)
				.faultTolerant()
				.reader(wiserIncrementalPriceDataReader)
				.processor(wiserPriceDataProcessor())
				.writer(wiserCsvPriceDataItemWriter)
				.build();
	}

	@Bean
	@Qualifier("writeFullWiserPriceData")
	public Step writeFullWiserPriceData(ItemStreamWriter<List<WiserPriceData>> wiserCsvPriceDataItemWriter) {
		return taskBatchJobFactory.getStepBuilder("writeFullWiserPriceData")
				.<WiserPriceData, List<WiserPriceData>>chunk(1000)
				.faultTolerant()
				.reader(wiserFullPriceDataReader())
				.processor(wiserPriceDataProcessor())
				.writer(wiserCsvPriceDataItemWriter)
				.build();
	}

	@Bean
	@Qualifier("writeWiserPerformanceData")
	public Step writeWiserPerformanceData(MyBatisCursorItemReader<WiserPerformanceData> wiserPerformanceDataReader, ItemStreamWriter<WiserPerformanceData> wiserCsvPerformanceDataItemWriter) {
		return taskBatchJobFactory.getStepBuilder("writeWiserPerformanceData")
				.<WiserPerformanceData, WiserPerformanceData>chunk(1000)
				.faultTolerant()
				.reader(wiserPerformanceDataReader)
				.processor(wiserPerformanceDataProcessor())
				.writer(wiserCsvPerformanceDataItemWriter)
				.build();
	}


	@Bean
	@Qualifier("uploadCsv")
	public Step uploadCsv(UploadFileTasklet uploadFileTasklet) {
		return taskBatchJobFactory.getStepBuilder("uploadCsv")
				.tasklet(uploadFileTasklet)
				.build();
	}

	@Bean
	@Qualifier("downloadCsv")
	public Step downloadCsv(DownloadFileTasklet downloadFileTasklet) {
		return taskBatchJobFactory.getStepBuilder("downloadCsv")
				.tasklet(downloadFileTasklet)
				.build();
	}

	private FlatFileItemWriter getFlatFileItemWriter(String[] fileHeader, String filePath, FieldExtractor fieldExtractor) {
		FlatFileItemWriter<?> fileItemWriter = new FlatFileItemWriter<>();

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
