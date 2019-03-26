package com.ferguson.cs.product.task.stylyze;

import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;
import com.ferguson.cs.product.task.stylyze.model.StylyzeProduct;
import com.ferguson.cs.task.util.DataFlowTempFileHelper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Configuration
public class StylyzeJobConfiguration {

	private final ProductService productService;
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private StylyzeSettings stylyzeSettings;

	StylyzeJobConfiguration(ProductService productService,
	                        JobBuilderFactory jobBuilderFactory,
	                        StepBuilderFactory stepBuilderFactory,
	                        StylyzeSettings stylyzeSettings) {

		this.productService = productService;
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.stylyzeSettings = stylyzeSettings;
	}

	@Value("${spring.application.name}")
	private String applicationName;

	private Resource getTempResource() throws IOException {
		File tempFile = DataFlowTempFileHelper.createTempFile("stylyze-output", "json");
		return new FileSystemResource(tempFile);
	}

	@Bean
	public ItemReader<StylyzeInputProduct> stylyzeProductReader() {
		return new ListItemReader<>(this.stylyzeSettings.getInputData());
	}

	@Bean
	public ItemProcessor<StylyzeInputProduct, StylyzeProduct> stylyzeProcessor() {
		return new StylyzeItemProcessor(productService, stylyzeSettings);
	}

	/**
	 * This function defines the writer that will be used to output the converted file.
	 * A chunk of data as defined by the job is written at a time.
	 *
	 * @return writer
	 */
	@Bean
	public JsonFileItemWriter<StylyzeProduct> stylyzeProductWriter() throws IOException {
		return new JsonFileItemWriterBuilder<StylyzeProduct>()
				.jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
				.resource(this.getTempResource())
				.name("stylyzeProductWriter")
				.build();
	}

	@Bean
	public Step stylyzeStep(ItemReader<StylyzeInputProduct> stylyzeProductReader,
	                        ItemProcessor<StylyzeInputProduct, StylyzeProduct> processor,
	                        JsonFileItemWriter<StylyzeProduct> writer) {
		return stepBuilderFactory.get("stylyzeStep")
				.<StylyzeInputProduct, StylyzeProduct>chunk(100)
				.reader(stylyzeProductReader)
				.processor(processor).writer(writer)
				.build();
	}

	@Bean(name = "stylyze")
	public Job stylyzeJob(@Qualifier("stylyzeStep") Step stylyzeStep) {
		return jobBuilderFactory.get("stylyze").start(stylyzeStep).build();
	}

}