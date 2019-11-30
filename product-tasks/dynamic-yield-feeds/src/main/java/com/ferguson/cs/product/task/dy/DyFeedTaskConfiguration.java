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
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.dy.batch.DynamicYieldProductDataProcessor;
import com.ferguson.cs.product.task.dy.batch.ProductDataSiteWriter;
import com.ferguson.cs.product.task.dy.batch.QuoteEnclosingDelimitedLineAggregator;
import com.ferguson.cs.product.task.dy.batch.UploadFileTasklet;
import com.ferguson.cs.product.task.dy.domain.Sites;
import com.ferguson.cs.product.task.dy.model.DynamicYieldProduct;
import com.ferguson.cs.product.task.dy.model.ProductData;
import com.ferguson.cs.product.task.dy.service.DyAsyncService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.task.util.DataFlowTempFileHelper;

@Configuration
public class DyFeedTaskConfiguration {
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final DyFeedSettings dyFeedSettings;
	private final DyAsyncService dyAsyncService;

	public DyFeedTaskConfiguration(SqlSessionFactory coreSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory,
	                               DyFeedSettings dyFeedSettings, DyAsyncService dyAsyncService) {
		this.reporterSqlSessionFactory = coreSqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.dyFeedSettings = dyFeedSettings;
		this.dyAsyncService = dyAsyncService;
	}

	@Bean
	public Map<Integer, Resource> dyProductFileResource() throws IOException {
		Map<Integer, Resource> siteFileMap = new HashMap<>();
		for (Sites site : Sites.values()) {
			siteFileMap.put(site.getSiteId(),
					new FileSystemResource(DataFlowTempFileHelper.createTempFile(site.getSiteId().toString() +
									dyFeedSettings.getTempFilePrefix(), dyFeedSettings.getTempFileSuffix()))
			);
		}
		return siteFileMap;
	}

	@Bean
	public MyBatisCursorItemReader<ProductData> productDataReader() {
		MyBatisCursorItemReader<ProductData> productDataReader = new MyBatisCursorItemReader<>();
		productDataReader.setQueryId("getProductData");
		productDataReader.setSqlSessionFactory(reporterSqlSessionFactory);
		return productDataReader;
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
				"configuration",
				"california_drought_compliant"
		};

		BeanWrapperFieldExtractor<DynamicYieldProduct> extractor = new BeanWrapperFieldExtractor<>();
		extractor.setNames(camelCase(header));

		ProductDataSiteWriter writer = new ProductDataSiteWriter();
		writer.setHeaderNames(header);
		writer.setDelimeter(DelimitedLineTokenizer.DELIMITER_COMMA);
		LineAggregator<DynamicYieldProduct> lineAggregator = createLineAggregator(extractor);
		writer.setLineAggregator(lineAggregator);
		writer.setDyProductFileResource(dyProductFileResource());
		return writer;
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
	@StepScope
	UploadFileTasklet uploadFileTasklet() throws IOException {
		return new UploadFileTasklet(dyFeedSettings, dyProductFileResource(), dyAsyncService);
	}

	@Bean
	public Step uploadCsv(UploadFileTasklet uploadFileTasklet) {
		return taskBatchJobFactory.getStepBuilder("uploadCsv")
				.tasklet(uploadFileTasklet)
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
				.next(uploadCsv(uploadFileTasklet()))
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

		for (String columnName : columnNames) {
			Matcher underscoreMatcher = Pattern.compile("_(.)").matcher(columnName);
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

	/**
	 * Create a QuoteEnclosingLineAggregator for for correct formatting of csv files
	 *
	 * @param fieldExtractor
	 * @return lineAggregator
	 */
	private LineAggregator<DynamicYieldProduct> createLineAggregator(FieldExtractor<DynamicYieldProduct> fieldExtractor) {
		QuoteEnclosingDelimitedLineAggregator<DynamicYieldProduct> lineAggregator = new QuoteEnclosingDelimitedLineAggregator<>();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(fieldExtractor);
		return lineAggregator;
	}
}
