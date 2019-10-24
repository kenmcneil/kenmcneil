package com.ferguson.cs.product.task.dy;

import java.io.IOException;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.dy.batch.DynamicYieldProductDataProcessor;
import com.ferguson.cs.product.task.dy.batch.QuoteEnclosingDelimitedLineAggregator;
import com.ferguson.cs.product.task.dy.batch.UploadFileTasklet;
import com.ferguson.cs.product.task.dy.domain.ResourceObject;
import com.ferguson.cs.product.task.dy.model.DynamicYieldProduct;
import com.ferguson.cs.product.task.dy.model.ProductData;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class DyFeedTaskConfiguration {
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final DyFeedSettings dyFeedSettings;

	public DyFeedTaskConfiguration(SqlSessionFactory coreSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory,
	                               DyFeedSettings dyFeedSettings) {
		this.reporterSqlSessionFactory = coreSqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.dyFeedSettings = dyFeedSettings;
	}

	@Bean
	@JobScope
	public ResourceObject resourceObject() throws IOException {
		return new ResourceObject(dyFeedSettings.getTempFilePrefix(), dyFeedSettings.getTempFileSuffix());
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
	public DynamicYieldProductDataProcessor dyProductDataProcessor() {
		return new DynamicYieldProductDataProcessor();
	}

	@Bean
	@StepScope
	ItemStreamWriter<DynamicYieldProduct> dyCsvProductItemWriter() throws IOException {
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
				"configuration"
		};

		BeanWrapperFieldExtractor<DynamicYieldProduct> extractor = new BeanWrapperFieldExtractor<>();
		extractor.setNames(new String[]{
				"sku",
				"groupId",
				"name",
				"url",
				"price",
				"inStock",
				"imageUrl",
				"categories",
				"model",
				"manufacturer",
				"discontinued",
				"series",
				"theme",
				"genre",
				"finish",
				"rating",
				"hasImage",
				"relativePath",
				"type",
				"application",
				"handletype",
				"masterfinish",
				"mountingType",
				"installationType",
				"numberOfBasins",
				"nominalLength",
				"nominalWidth",
				"numberOfLights",
				"chandelierType",
				"pendantType",
				"fanType",
				"fuelType",
				"configuration"
		});
		return getFlatFileItemWriter(header, resourceObject().getResource().getPath(), extractor);
	}

	@Bean
	@Qualifier("writeDyItems")
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
	UploadFileTasklet uploadFileTasklet() {
		return new UploadFileTasklet();
	}

	@Bean
	@Qualifier("uploadCsv")
	public Step uploadCsv(UploadFileTasklet uploadFileTasklet) {
		return taskBatchJobFactory.getStepBuilder("uploadCsv")
				.tasklet(uploadFileTasklet)
				.build();
	}

	/**
	 * Writes product data matching all product hashes that have changed since the last time this job was run to a
	 * csv, uploads that csv to Dynamic Yield. This will send the entire product catalog.
	 */
	@Bean
	@Qualifier("dynamicYieldExportJob")
	public Job dynamicYieldExportJob(Step writeDyItems) {
		return taskBatchJobFactory.getJobBuilder("dynamicYieldExportJob")
				.start(writeDyItems)
				.next(uploadCsv(uploadFileTasklet()))
				.build();
	}

	private FlatFileItemWriter<DynamicYieldProduct> getFlatFileItemWriter(String[] fileHeader, String filePath, FieldExtractor<DynamicYieldProduct> fieldExtractor) {
		FlatFileItemWriter<DynamicYieldProduct> fileItemWriter = new FlatFileItemWriter<>();

		fileItemWriter.setHeaderCallback(writer -> writer.write(String.join(DelimitedLineTokenizer.DELIMITER_COMMA, fileHeader)));
		fileItemWriter.setResource(new FileSystemResource(filePath));

		LineAggregator<DynamicYieldProduct> lineAggregator = createLineAggregator(fieldExtractor);
		fileItemWriter.setLineAggregator(lineAggregator);

		return fileItemWriter;
	}

	private LineAggregator<DynamicYieldProduct> createLineAggregator(FieldExtractor<DynamicYieldProduct> fieldExtractor) {
		QuoteEnclosingDelimitedLineAggregator<DynamicYieldProduct> lineAggregator = new QuoteEnclosingDelimitedLineAggregator<>();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(fieldExtractor);
		return lineAggregator;
	}

}
