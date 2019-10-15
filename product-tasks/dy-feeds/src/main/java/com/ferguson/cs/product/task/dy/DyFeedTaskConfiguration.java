package com.ferguson.cs.product.task.dy;

import java.io.File;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.dy.batch.DynamicYieldProductDataProcessor;
import com.ferguson.cs.product.task.dy.batch.QuoteEnclosingDelimitedLineAggregator;
import com.ferguson.cs.product.task.dy.model.ProductData;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class DyFeedTaskConfiguration {
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final DyFeedSettings dyFeedSettings;

	public DyFeedTaskConfiguration(SqlSessionFactory coreSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory, DyFeedSettings dyFeedSettings) {
		this.reporterSqlSessionFactory = coreSqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.dyFeedSettings = dyFeedSettings;
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
	ItemStreamWriter<ProductData> dyCsvProductItemWriter(@Value("#{jobExecutionContext['fileName']}") String fileName) {
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

		BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
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
		return getFlatFileItemWriter(header, dyFeedSettings.getLocalFilePath() + dyFeedSettings.getLocalFileName(), extractor);
	}

	@Bean
	@Qualifier("writeDyItems")
	public Step writeDyItems(ItemStreamWriter<ProductData> dyCsvCatalogItemWriter) {

		return taskBatchJobFactory.getStepBuilder("writeDyItems")
				.<ProductData, ProductData>chunk(1000)
				.reader(productDataReader())
				.processor(dyProductDataProcessor())
				.writer(dyCsvCatalogItemWriter)
				.build();
	}

	/**
	 * Writes product data matching all product hashes that have changed since the last time this job was run to a
	 * csv, uploads that csv to Wiser. If this has never been run, or if the related data is deleted, this will send
	 * the entire product catalog/all product data hashes.
	 */
	@Bean
	@Qualifier("dynamicYieldExportJob")
	public Job dynamicYieldExportJob(Step writeDyItems) {
		return taskBatchJobFactory.getJobBuilder("dynamicYieldExportJob")
				.start(writeDyItems)
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
