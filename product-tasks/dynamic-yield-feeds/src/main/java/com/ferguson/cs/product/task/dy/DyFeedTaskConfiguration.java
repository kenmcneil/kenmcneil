package com.ferguson.cs.product.task.dy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.ferguson.cs.product.task.dy.batch.CustomMultiResourcePartitioner;
import com.ferguson.cs.product.task.dy.batch.DynamicYieldProductDataProcessor;
import com.ferguson.cs.product.task.dy.batch.ProductDataSiteWriter;
import com.ferguson.cs.product.task.dy.batch.UploadFileTasklet;
import com.ferguson.cs.product.task.dy.domain.SiteProductFileResource;
import com.ferguson.cs.product.task.dy.model.DynamicYieldProduct;
import com.ferguson.cs.product.task.dy.model.ProductData;
import com.ferguson.cs.product.task.dy.service.DyService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.task.util.DataFlowTempFileHelper;

@Configuration
public class DyFeedTaskConfiguration {
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final DyFeedSettings dyFeedSettings;
	private final DyService dyService;

	public DyFeedTaskConfiguration(SqlSessionFactory coreSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory,
	                               DyFeedSettings dyFeedSettings, DyService dyService) {
		this.reporterSqlSessionFactory = coreSqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.dyFeedSettings = dyFeedSettings;
		this.dyService = dyService;
	}

	@Bean
	public SiteProductFileResource dyProductFileResource() throws IOException {
		SiteProductFileResource siteProductFileResource = new SiteProductFileResource();


		for (Integer siteId : dyFeedSettings.getSiteUsername().keySet()) {
			siteProductFileResource.getSiteFileMap().put(siteId,
					new FileSystemResource(DataFlowTempFileHelper.createTempFile(siteId.toString() +
							dyFeedSettings.getTempFilePrefix(), dyFeedSettings.getTempFileSuffix()))
			);
		}
		return siteProductFileResource;
	}

	@Bean
	@StepScope
	public DynamicYieldProductDataProcessor dyProductDataProcessor() {
		return new DynamicYieldProductDataProcessor();
	}

	@Bean
	@StepScope
	public ProductDataSiteWriter dyCsvProductItemWriter() throws IOException {

		String[] header = new String[]{
				"sku",
				"group_id",
				"name",
				"url",
				"price",
				"in_stock",
				"image_url",
				"categories",
				"model",
				"manufacturer",
				"discontinued",
				"series",
				"theme",
				"genre",
				"finish",
				"rating",
				"has_image",
				"relative_path",
				"type",
				"application",
				"handletype",
				"masterfinish",
				"mounting_type",
				"installation_type",
				"number_of_basins",
				"nominal_length",
				"nominal_width",
				"number_of_lights",
				"chandelier_type",
				"pendant_type",
				"fan_type",
				"fuel_type",
				"base_category",
				"business_category",
				"configuration",
				"california_drought_compliant",
				"keywords"
		};

		BeanWrapperFieldExtractor<DynamicYieldProduct> extractor = new BeanWrapperFieldExtractor<>();
		extractor.setNames(camelCase(header));

		ProductDataSiteWriter writer = new ProductDataSiteWriter();
		writer.setHeaderNames(header);
		writer.setDelimeter(DelimitedLineTokenizer.DELIMITER_COMMA);
		writer.setExtractor(extractor);
		writer.setDyProductFileResource(dyProductFileResource());
		return writer;
	}

	@Bean
	public MyBatisCursorItemReader<ProductData> productDataReader() {
		MyBatisCursorItemReader<ProductData> productDataReader = new MyBatisCursorItemReader<>();
		productDataReader.setQueryId("getProductData");
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("storeIds", dyFeedSettings.getStores());
		parameters.put("restrictionPolicies", dyFeedSettings.getRestrictionPolicies());
		productDataReader.setParameterValues(parameters);
		productDataReader.setSqlSessionFactory(reporterSqlSessionFactory);
		return productDataReader;
	}

	@Bean
	public Step writeDyItems(ItemStreamWriter<DynamicYieldProduct> dyCsvCatalogItemWriter) {

		return taskBatchJobFactory.getStepBuilder("writeDyItems")
				.<ProductData, DynamicYieldProduct>chunk(1000)
				.reader(productDataReader())
				.processor(dyProductDataProcessor())
				.writer(dyCsvCatalogItemWriter)
				.build();
	}


	@Bean
	public Step partitionFtpStep() throws IOException {
		return taskBatchJobFactory.getStepBuilder("partitionFtpStep")
				.partitioner(uploadCsv(uploadFileTasklet(null, null)))
				.partitioner("uploadCsv", partitioner())
				.gridSize(10)
				.taskExecutor(new SimpleAsyncTaskExecutor())
				.build();
	}

	@Bean
	public CustomMultiResourcePartitioner partitioner() throws IOException {
		CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
		partitioner.setResources(dyProductFileResource());
		return partitioner;
	}

	@StepScope
	@Bean
	UploadFileTasklet uploadFileTasklet(
			@Value("#{stepExecutionContext[fileName]}") String filename,
			@Value("#{stepExecutionContext[siteId]}") Integer siteId) throws IOException {

		return new UploadFileTasklet(filename, siteId, dyFeedSettings, dyService);
	}

	@Bean
	public Step uploadCsv(UploadFileTasklet uploadFileTasklet) throws IOException {
		return taskBatchJobFactory.getStepBuilder("uploadCsv")
				.tasklet(uploadFileTasklet(null, null))
				.build();
	}

	/**
	 * Writes product data matching all product hashes that have changed since the last time this job was run to a
	 * csv, uploads that csv to Dynamic Yield. This will send the entire product catalog for all sites defined in
	 * the Sites enum
	 */
	@Bean
	public Job dynamicYieldExportJob(Step writeDyItems) throws IOException {
		return taskBatchJobFactory.getJobBuilder("dynamicYieldExportJob")
				.start(writeDyItems)
				.next(partitionFtpStep())
				.build();
	}

	/**
	 * Convert snake case to camel case
	 *
	 * @param columnNames
	 * @return Camel cased string
	 */
	private String[] camelCase(String[] columnNames) {
		String[] updatedColumnNames = new String[columnNames.length];
		int index = 0;
		Pattern underscorePattern = Pattern.compile("_(.)");

		for (String columnName : columnNames) {
			Matcher underscoreMatcher = underscorePattern.matcher(columnName);
			StringBuffer sb = new StringBuffer();

			while (underscoreMatcher.find()) {
				underscoreMatcher.appendReplacement(sb, underscoreMatcher.group().toUpperCase());
			}

			underscoreMatcher.appendTail(sb);
			updatedColumnNames[index] = sb.toString().replaceAll("_","");
			index++;
		}
		return updatedColumnNames;
	}
}
