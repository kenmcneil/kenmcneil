package com.ferguson.cs.product.task.wiser;

import java.io.File;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
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
import com.ferguson.cs.product.task.wiser.batch.PopulateProductRevenueCategorizationTasklet;
import com.ferguson.cs.product.task.wiser.batch.ProductDataHashProcessor;
import com.ferguson.cs.product.task.wiser.batch.QuoteEnclosingDelimitedLineAggregator;
import com.ferguson.cs.product.task.wiser.batch.SetItemWriter;
import com.ferguson.cs.product.task.wiser.batch.TruncateProductDataHashTasklet;
import com.ferguson.cs.product.task.wiser.batch.UploadCostTasklet;
import com.ferguson.cs.product.task.wiser.batch.UploadFileTasklet;
import com.ferguson.cs.product.task.wiser.batch.WiserFeedListener;
import com.ferguson.cs.product.task.wiser.batch.WiserPerformanceDataProcessor;
import com.ferguson.cs.product.task.wiser.batch.WiserPriceDataProcessor;
import com.ferguson.cs.product.task.wiser.batch.WiserProductDataProcessor;
import com.ferguson.cs.product.task.wiser.batch.WiserRecommendationFeedProcessor;
import com.ferguson.cs.product.task.wiser.batch.WiserRecommendationFeedReader;
import com.ferguson.cs.product.task.wiser.batch.WiserRetryableJobTasklet;
import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserFeedType;
import com.ferguson.cs.product.task.wiser.model.WiserPerformanceData;
import com.ferguson.cs.product.task.wiser.model.WiserPriceData;
import com.ferguson.cs.product.task.wiser.model.WiserProductData;
import com.ferguson.cs.product.task.wiser.model.WiserRecommendationData;
import com.ferguson.cs.product.task.wiser.service.WiserService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.task.batch.util.JobRepositoryHelper;
import com.ferguson.cs.utilities.DateUtils;

@Configuration
public class WiserFeedTaskConfiguration {
	private WiserService wiserService;
	private SqlSessionFactory reporterSqlSessionFactory;
	private SqlSessionFactory integrationSqlSessionFactory;
	private SqlSessionFactory batchSqlSessionFactory;
	private WiserFeedSettings wiserFeedSettings;
	private WiserFeedConfiguration.WiserGateway wiserGateway;
	private TaskBatchJobFactory taskBatchJobFactory;

	private static final Logger LOG = LoggerFactory.getLogger(WiserFeedTaskConfiguration.class);

	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}

	@Autowired
	@Qualifier("reporterSqlSessionFactory")
	public void setReporterSqlSessionFactory(SqlSessionFactory reporterSqlSessionFactory) {
		this.reporterSqlSessionFactory = reporterSqlSessionFactory;
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

	@Autowired
	public void setWiserGateway(WiserFeedConfiguration.WiserGateway wiserGateway) {
		this.wiserGateway = wiserGateway;
	}

	@Bean
	public WiserRetryableJobTasklet wiserRetryableJobDecider(JobRepositoryHelper jobRepositoryHelper) {
		return new WiserRetryableJobTasklet(jobRepositoryHelper);
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<ProductData> productDataReader() {
		MyBatisCursorItemReader<ProductData> productDataReader = new MyBatisCursorItemReader<>();
		productDataReader.setQueryId("getProductData");
		productDataReader.setSqlSessionFactory(reporterSqlSessionFactory);
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
		if (date == null) {
			date = DateUtils.addDaysToDate(new Date(), -1);
		}
		MyBatisCursorItemReader<WiserPerformanceData> wiserPerformanceDataReader = new MyBatisCursorItemReader<>();
		wiserPerformanceDataReader.setQueryId("getWiserPerformanceData");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("date", date);
		wiserPerformanceDataReader.setParameterValues(parameters);
		wiserPerformanceDataReader.setSqlSessionFactory(reporterSqlSessionFactory);
		return wiserPerformanceDataReader;
	}

	@Bean
	@StepScope
	public WiserRecommendationFeedReader wiserRecommendationFeedReader(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		WiserRecommendationFeedReader wiserRecommendationFeedReader = new WiserRecommendationFeedReader(wiserService, fileName);
		wiserRecommendationFeedReader.setName("wiserRecommendationFeedReader");
		return wiserRecommendationFeedReader;
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
		wiserPriceDataReader.setSqlSessionFactory(reporterSqlSessionFactory);
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
		writer.setAssertUpdates(false);
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
	public WiserRecommendationFeedProcessor wiserRecommendationFeedProcessor() {
		return new WiserRecommendationFeedProcessor();
	}

	@Bean
	@StepScope
	UploadFileTasklet uploadFileTasklet(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		return new UploadFileTasklet(wiserFeedSettings.getTemporaryLocalFilePath() + fileName);
	}


	@Bean
	@StepScope
	DownloadFileTasklet threeSixtyPiDownloadFileTasklet() {
		return new DownloadFileTasklet(f -> {
			File file = wiserGateway.receive360piFileSftp(f);
			wiserGateway.deleteWiserFileSftp(f.getRemoteFilePath());
			return file;
		});
	}

	@Bean
	@StepScope
	DownloadFileTasklet wiserDownloadFileTasklet() {
		return new DownloadFileTasklet(f -> wiserGateway.receiveWiserFileSftp(f));
	}

	@Bean
	@StepScope
	TruncateProductDataHashTasklet truncateProductDataHashTasklet() {
		return new TruncateProductDataHashTasklet();
	}

	@Bean
	@StepScope
	UploadCostTasklet uploadCostTasklet() {
		return new UploadCostTasklet();
	}

	@Bean
	@StepScope
	PopulateProductRevenueCategorizationTasklet populateProductRevenueCategorizationTasklet(WiserService wiserService) {
		return new PopulateProductRevenueCategorizationTasklet(wiserService);
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
		return getFlatFileItemWriter(header, wiserFeedSettings.getTemporaryLocalFilePath() + fileName, extractor);
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

		ItemStreamWriter<WiserPriceData> innerWriter = getFlatFileItemWriter(header, wiserFeedSettings
				.getTemporaryLocalFilePath() + fileName, extractor);

		return new FlatteningItemStreamWriter<>(innerWriter);
	}

	@Bean
	@StepScope
	ItemStreamWriter<WiserPerformanceData> wiserCsvPerformanceDataItemWriter(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		String[] header = new String[]{"sku",
				"transaction_date",
				"gross_units",
				"gross_orders",
				"gross_revenue",
				"channel",
				"ncr",
				"marketplace_id"};

		BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
		extractor.setNames(new String[]{"sku",
				"transactionDate",
				"grossUnits",
				"grossOrders",
				"grossRevenue",
				"channel",
				"ncr",
				"marketplaceId"});

		return getFlatFileItemWriter(header, wiserFeedSettings.getTemporaryLocalFilePath() + fileName, extractor);
	}

	@Bean
	@StepScope
	public ItemStreamReader<String> wiserRecommendationFileReader(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		return new FlatFileItemReaderBuilder<String>()
				.resource(new FileSystemResource(wiserFeedSettings.getTemporaryLocalFilePath() + fileName))
				.linesToSkip(1).name("wiserRecommendationFileReader").lineMapper(new PassThroughLineMapper()).build();

	}

	@Bean
	@StepScope
	ItemStreamWriter<WiserRecommendationData> wiserRecommendationFileWriter(@Value("#{jobExecutionContext['fileName']}") String fileName) {
		String[] header = new String[]{"UniqueId", "PricebookId", "Cost"};

		BeanWrapperFieldExtractor<WiserRecommendationData> fieldExtractor = new BeanWrapperFieldExtractor<>();

		fieldExtractor.setNames(new String[]{"uniqueId", "pricebookId", "cost"});

		return getFlatFileItemWriter(header, wiserFeedSettings.getFileDownloadLocation() + fileName, fieldExtractor);
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

	@Bean
	@JobScope
	public WiserFeedListener wiserRecommendationFeedListener() {
		return new WiserFeedListener(WiserFeedType.RECOMMENDATION_FEED);
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
	public Job repopulateProductDataHashJob(Step truncateProductDataHashes, Step insertProductDataHash) {
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
	public Job productCatalogFullUploadJob(Step decideIfJobShouldRun, Step populateProductRevenueCategorization, Step writeWiserItems, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("productCatalogFullUploadJob")
				.listener(wiserProductCatalogFeedListener())
				.start(decideIfJobShouldRun)
				.on(ExitStatus.NOOP.getExitCode()).end()
				.from(decideIfJobShouldRun)
				.on(ExitStatus.COMPLETED.getExitCode())
				.to(populateProductRevenueCategorization)
				.next(writeWiserItems)
				.next(uploadCsv)
				.end()
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
	public Job priceDataFullUploadJob(Step decideIfJobShouldRun, Step writeFullWiserPriceData, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("priceDataFullUploadJob")
				.listener(wiserPriceFeedListener())
				.start(decideIfJobShouldRun)
				.on(ExitStatus.NOOP.getExitCode()).end()
				.from(decideIfJobShouldRun)
				.on(ExitStatus.COMPLETED.getExitCode())
				.to(writeFullWiserPriceData)
				.next(uploadCsv)
				.end()
				.build();
	}

	@Bean
	@Qualifier("competitorDataFeedJob")
	public Job competitorDataFeedJob(Step decideIfJobShouldRun, Step uploadCsv) {
		Step downloadCsv = downloadCsv(threeSixtyPiDownloadFileTasklet());
		return taskBatchJobFactory.getJobBuilder("competitorDataFeedJob")
				.listener(wiserCompetitorFeedListener())
				.start(decideIfJobShouldRun)
				.on(ExitStatus.NOOP.getExitCode()).end()
				.from(decideIfJobShouldRun)
				.on(ExitStatus.COMPLETED.getExitCode())
				.to(downloadCsv)
				.next(uploadCsv)
				.end()
				.build();
	}

	@Bean
	@Qualifier("performanceDataUploadJob")
	public Job performanceDataUploadJob(Step decideIfJobShouldRun, Step writeWiserPerformanceData, Step uploadCsv) {
		return taskBatchJobFactory.getJobBuilder("performanceDataUploadJob")
				.listener(wiserPerformanceFeedListener())
				.start(decideIfJobShouldRun)
				.on(ExitStatus.NOOP.getExitCode()).end()
				.from(decideIfJobShouldRun)
				.on(ExitStatus.COMPLETED.getExitCode())
				.to(writeWiserPerformanceData)
				.next(uploadCsv)
				.end()
				.build();
	}

	@Bean
	@Qualifier("recommendationDataDownloadJob")
	public Job recommendationDataDownloadJob(Step decideIfJobShouldRun, Step uploadCost, Step processRecommendationFeed) {
		Step downloadCsv = downloadCsv(wiserDownloadFileTasklet());
		return taskBatchJobFactory.getJobBuilder("recommendationDataDownloadJob")
				.listener(wiserRecommendationFeedListener())
				.start(decideIfJobShouldRun)
				.on(ExitStatus.NOOP.getExitCode()).end()
				.from(decideIfJobShouldRun)
				.on(ExitStatus.COMPLETED.getExitCode())
				.to(downloadCsv)
				.next(processRecommendationFeed)
				.next(uploadCost)
				.end()
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
	@Qualifier("processRecommendationFeed")
	public Step processRecommendationFeed(ItemStreamReader<WiserRecommendationData> wiserRecommendationFeedReader,
										  ItemStreamWriter<WiserRecommendationData> wiserRecommendationFileWriter,
										  ItemProcessor<WiserRecommendationData, WiserRecommendationData> wiserRecommendationFeedProcessor) {
		return taskBatchJobFactory.getStepBuilder("processRecommendationFeed")
				.<WiserRecommendationData, WiserRecommendationData>chunk((1000))
				.faultTolerant()
				.reader(wiserRecommendationFeedReader)
				.processor(wiserRecommendationFeedProcessor)
				.writer(wiserRecommendationFileWriter)
				.build();
	}

	@Bean
	@Qualifier("uploadCost")
	public Step uploadCost(UploadCostTasklet uploadCostTasklet) {
		return taskBatchJobFactory.getStepBuilder("uploadCost")
				.tasklet(uploadCostTasklet)
				.build();
	}

	@Bean
	@Qualifier("decideIfJobShouldRun")
	public Step decideIfJobShouldRun(WiserRetryableJobTasklet wiserRetryableJobTasklet) {
		return taskBatchJobFactory.getStepBuilder("decideIfJobShouldRun")
				.tasklet(wiserRetryableJobTasklet)
				.build();
	}

	@Bean
	@Qualifier("populateProductRevenueCategorization")
	public Step populateProductRevenueCategorization(PopulateProductRevenueCategorizationTasklet populateProductRevenueCategorizationTasklet) {
		return taskBatchJobFactory.getStepBuilder("populateProductRevenueCategorization")
				.tasklet(populateProductRevenueCategorizationTasklet)
				.build();
	}

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
