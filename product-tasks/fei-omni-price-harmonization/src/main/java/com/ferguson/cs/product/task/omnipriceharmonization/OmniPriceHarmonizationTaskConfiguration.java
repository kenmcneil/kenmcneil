package com.ferguson.cs.product.task.omnipriceharmonization;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.omnipriceharmonization.batch.FileHandlingTasklet;
import com.ferguson.cs.product.task.omnipriceharmonization.batch.OmniPriceHarmonizationProcessor;
import com.ferguson.cs.product.task.omnipriceharmonization.batch.TruncatePricingDataTasklet;
import com.ferguson.cs.product.task.omnipriceharmonization.model.PriceHarmonizationData;
import com.ferguson.cs.product.task.omnipriceharmonization.service.OmniPriceHarmonizationService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class OmniPriceHarmonizationTaskConfiguration {
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final OmniPriceHarmonizationService feiPriceService;
	private final OmniPriceHarmonizationSettings omniPriceHarmonizationSettings;

	private static final Logger LOGGER = LoggerFactory.getLogger(OmniPriceHarmonizationTaskConfiguration.class);

	public OmniPriceHarmonizationTaskConfiguration(@Qualifier("reporterSqlSessionFactory") SqlSessionFactory reporterSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory, OmniPriceHarmonizationService feiPriceService, OmniPriceHarmonizationSettings omniPriceHarmonizationSettings) {
		this.reporterSqlSessionFactory = reporterSqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.feiPriceService = feiPriceService;
		this.omniPriceHarmonizationSettings = omniPriceHarmonizationSettings;
	}

	@Bean
	@StepScope
	public MyBatisBatchItemWriter<PriceHarmonizationData> omniPriceHarmonizationWriter() {
		MyBatisBatchItemWriter<PriceHarmonizationData> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(reporterSqlSessionFactory);
		writer.setStatementId("insertPriceHarmonizationData");
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public FlatFileItemReader<PriceHarmonizationData> omniPriceHarmonizationReader() {
		File[] files = new File(omniPriceHarmonizationSettings.getIncomingFilePath()).listFiles();
		File newestOmniPriceHarmonizationFile = null;

		for(File file : files) {
			if(FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")) {
				if(newestOmniPriceHarmonizationFile == null || file.lastModified() > newestOmniPriceHarmonizationFile.lastModified()) {
					newestOmniPriceHarmonizationFile = file;
				}
			}
		}

		if(newestOmniPriceHarmonizationFile == null) {
			LOGGER.error("Could not find an omni pricing harmonization file");
			throw new RuntimeException("Could not find an omni pricing harmonization file");
		}

		return new FlatFileItemReaderBuilder<PriceHarmonizationData>().fieldSetMapper(new BeanWrapperFieldSetMapper<>()).targetType(PriceHarmonizationData.class).linesToSkip(1).delimited().delimiter(",").names("mpid","uniqueId","dg","pc24","masterList","imap").name("omniPriceHarmonizationReader").resource(new FileSystemResource(newestOmniPriceHarmonizationFile)).build();
	}

	@Bean
	@StepScope
	public OmniPriceHarmonizationProcessor omniPriceHarmonizationProcessor() {
		return new OmniPriceHarmonizationProcessor();
	}

	@Bean
	@StepScope
	public FileHandlingTasklet fileHandlingTasklet() {
		return new FileHandlingTasklet(omniPriceHarmonizationSettings);
	}

	@Bean
	@StepScope
	public TruncatePricingDataTasklet truncatePricingDataTasklet(OmniPriceHarmonizationService omniPriceHarmonizationService) {
		return new TruncatePricingDataTasklet(omniPriceHarmonizationService);
	}


	@Bean
	public Step truncateOmniPricingData(TruncatePricingDataTasklet truncatePricingDataTasklet) {
		return taskBatchJobFactory.getStepBuilder("truncateOmniPricingData").tasklet(truncatePricingDataTasklet).build();
	}

	@Bean
	public Step archiveFiles(FileHandlingTasklet fileHandlingTasklet) {
		return taskBatchJobFactory.getStepBuilder("archiveFiles").tasklet(fileHandlingTasklet).build();
	}

	@Bean
	public Step writeOmniPriceHarmonizationData(MyBatisBatchItemWriter<PriceHarmonizationData> omniPriceHarmonizationWriter, FlatFileItemReader<PriceHarmonizationData> omniPriceHarmonizationReader, OmniPriceHarmonizationProcessor omniPriceHarmonizationProcessor) {
		return taskBatchJobFactory.getStepBuilder("writeOmniPriceHarmonizationData").<PriceHarmonizationData, PriceHarmonizationData>chunk(10000)
				.reader(omniPriceHarmonizationReader).processor(omniPriceHarmonizationProcessor).writer(omniPriceHarmonizationWriter).build();
	}

	@Bean
	public Job importPriceHarmonizationReport(Step truncateOmniPricingData, Step writeOmniPriceHarmonizationData, Step archiveFiles) {
		return taskBatchJobFactory.getJobBuilder("importPriceHarmonizationReport").start(truncateOmniPricingData).next(writeOmniPriceHarmonizationData).next(archiveFiles).build();
	}
}
