package com.ferguson.cs.product.task.feipriceupdate;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemProcessor;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemWriter;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateJobListener;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateReadListener;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateTempTableTasklet;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateDao;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateDaoImpl;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateMapper;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class FeiPriceUpdateTaskConfiguration {
	
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;

	public FeiPriceUpdateTaskConfiguration(SqlSessionFactory sqlSessionFactory, 
			TaskBatchJobFactory taskBatchJobFactory, 
			FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}
	
	@Bean
	public FeiPriceUpdateDao feiPriceUpdateDao(FeiPriceUpdateMapper feiPriceUpdateMapper) {
		return new FeiPriceUpdateDaoImpl(feiPriceUpdateMapper);
	}
	
	
	@Bean
	public LineMapper<FeiPriceUpdateItem> feiPriceUpdateItemLineMapper() {
		DefaultLineMapper<FeiPriceUpdateItem> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
		lineTokenizer.setNames("uniqueId", "price", "priceRule", "mpid");
		BeanWrapperFieldSetMapper<FeiPriceUpdateItem> mapper = new BeanWrapperFieldSetMapper<>();
		mapper.setTargetType(FeiPriceUpdateItem.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(mapper);
		return lineMapper;
	}
	
	  @Bean
	  public FeiPriceUpdateItemProcessor processor() {
	    return new FeiPriceUpdateItemProcessor();
	  }
	  
	  @Bean
	  public Job feiUpdatePriceJob( FeiPriceUpdateSettings feiPriceUpdateSettings,
			  FeiPriceUpdateService feiPriceUpdateService) {
	    return taskBatchJobFactory.getJobBuilder("feiPriceUpdateJob")
	        .listener(new FeiPriceUpdateJobListener(feiPriceUpdateSettings,feiPriceUpdateService))
			.start(createTempTableStep(feiPriceUpdateSettings,feiPriceUpdateService))
	        .next(processInputFile(feiPriceUpdateSettings,feiPriceUpdateService))
	        .build();
	  }
	  
	  @Bean
	  public Step processInputFile(
			  FeiPriceUpdateSettings feiPriceUpdateSettings,
			  FeiPriceUpdateService feiPriceUpdateService) {
	    return taskBatchJobFactory.getStepBuilder("processInputFile")
	        .<FeiPriceUpdateItem, FeiPriceUpdateItem>chunk(100)
	        .reader(allFilesReader())
	        .listener(feiPriceUpdateReadListener(feiPriceUpdateSettings))
	        .faultTolerant()
	        .processor(processor())
	        .writer(feiPriceUpdateItemWriter(feiPriceUpdateService))
	        .build();
	  }
	  
	/*
	 * Setup a tasklet to create the temp DB table
	 *
	 * @return Step
	 */
	@Bean
	public Step createTempTableStep(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return taskBatchJobFactory.getStepBuilder("createTempTableStep")
				.tasklet(feiPriceUpdateTempTableTasklet(feiPriceUpdateSettings,feiPriceUpdateService))
				.build();
	}

	@Bean
	@JobScope
	public FeiPriceUpdateJobListener feiPriceUpdateJobListener(
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return new FeiPriceUpdateJobListener(feiPriceUpdateSettings,feiPriceUpdateService);
	}
	
	@Bean
	@JobScope
	public FeiPriceUpdateReadListener feiPriceUpdateReadListener(
			FeiPriceUpdateSettings feiPriceUpdateSettings) {
		return new FeiPriceUpdateReadListener(feiPriceUpdateSettings);
	}
	  
	@Bean
	@StepScope
	public FlatFileItemReader<FeiPriceUpdateItem> feiPriceUpdateItemReader() {
		FlatFileItemReader<FeiPriceUpdateItem> reader = new FlatFileItemReader<>();
		reader.setLineMapper(feiPriceUpdateItemLineMapper());
		reader.setLinesToSkip(1);
		return reader;
	}
	
	
	@Bean
	@StepScope
	public MultiResourceItemReader<FeiPriceUpdateItem> allFilesReader() {
		Resource[] resources;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		try {
			resources = patternResolver.getResources("file:" + feiPriceUpdateSettings.getInputFilePath() + "*.csv");
		} catch (IOException e) {
			return null;
		}
		
		MultiResourceItemReader<FeiPriceUpdateItem> resourceItemReader = new MultiResourceItemReader<FeiPriceUpdateItem>();
		resourceItemReader.setResources(resources);
		resourceItemReader.setDelegate(feiPriceUpdateItemReader());
		return resourceItemReader;
	}
	
	@Bean
	@StepScope
	public ItemWriter<FeiPriceUpdateItem> feiPriceUpdateItemWriter(FeiPriceUpdateService  feiPriceUpdateService) {
		return new FeiPriceUpdateItemWriter(feiPriceUpdateService);
	}
	
	@Bean
	@StepScope
	public FeiPriceUpdateTempTableTasklet feiPriceUpdateTempTableTasklet(
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService  feiPriceUpdateService) {
		return new FeiPriceUpdateTempTableTasklet(feiPriceUpdateSettings,feiPriceUpdateService);
	}
	
}
