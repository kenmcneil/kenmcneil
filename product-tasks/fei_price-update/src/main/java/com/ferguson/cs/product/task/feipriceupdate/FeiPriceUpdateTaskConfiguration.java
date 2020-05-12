package com.ferguson.cs.product.task.feipriceupdate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
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

import com.ferguson.cs.product.task.feipriceupdate.batch.CreateCostUpdateJobTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemProcessor;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemWriter;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateJobListener;
import com.ferguson.cs.product.task.feipriceupdate.batch.CreateTempTableTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPricebookWriter;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceBookProcessor;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateDao;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateDaoImpl;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateMapper;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class FeiPriceUpdateTaskConfiguration {
	
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final SqlSessionFactory sqlSessionFactory;


	public FeiPriceUpdateTaskConfiguration(SqlSessionFactory sqlSessionFactory, 
			TaskBatchJobFactory taskBatchJobFactory, 
			FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.sqlSessionFactory = sqlSessionFactory;
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
	        .next(processInputFileStep(feiPriceUpdateSettings,feiPriceUpdateService))
	        .next(createCostUploadJobTasklet(feiPriceUpdateSettings, feiPriceUpdateService))
	        .next(createCostUploadJobStep(feiPriceUpdateSettings, feiPriceUpdateService))
	        .build();
	  }
	  
	  /*
	   * Read the input price update CSV file and load the tempDB table in preparation
	   * for the next step.
	   */
	  @Bean
	  public Step processInputFileStep(
			  FeiPriceUpdateSettings feiPriceUpdateSettings,
			  FeiPriceUpdateService feiPriceUpdateService) {
	    return taskBatchJobFactory.getStepBuilder("processInputFile")
	        .<FeiPriceUpdateItem, FeiPriceUpdateItem>chunk(1000)
	        .reader(allFilesReader())
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
	public Step createCostUploadJobTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return taskBatchJobFactory.getStepBuilder("createCostUploadJobTasklet")
				.tasklet(costUploadJobTasklet(feiPriceUpdateSettings,feiPriceUpdateService))
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
	public CreateTempTableTasklet feiPriceUpdateTempTableTasklet(
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService  feiPriceUpdateService) {
		return new CreateTempTableTasklet(feiPriceUpdateSettings,feiPriceUpdateService);
	}
	
	@Bean
	@StepScope
	public CreateCostUpdateJobTasklet costUploadJobTasklet(
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService  feiPriceUpdateService) {
		return new CreateCostUpdateJobTasklet(feiPriceUpdateSettings,feiPriceUpdateService);
	}
	
	
	// Apply pricing calculations step and create job:
	  @Bean
	  @StepScope
	  public FeiPriceBookProcessor priceCalculationProcessor() {
	    return new FeiPriceBookProcessor();
	  }
	  
	@Bean
	@StepScope
	public ItemWriter<PriceBookSync> feiPricebookWriter(FeiPriceUpdateService  feiPriceUpdateService) {
		return new FeiPricebookWriter(feiPriceUpdateService);
	}
	
	@Bean
	@StepScope
	public MyBatisCursorItemReader<FeiPriceUpdateItem> tempDataFeiPriceUpdateDataReader() {
		MyBatisCursorItemReader<FeiPriceUpdateItem> reader = new MyBatisCursorItemReader<>();
		Map<String, Object> parameters = new HashMap<>();
		FeiPriceUpdateItem item = new FeiPriceUpdateItem();
		item.setTempTableName(feiPriceUpdateSettings.getTempTableName());
		parameters.put("item", item);
		reader.setParameterValues(parameters);
		reader.setQueryId("getTempFeiPriceUpdateData");
		reader.setSqlSessionFactory(sqlSessionFactory);
		return reader;
	}
	
	 @Bean
	  public Step createCostUploadJobStep(
			  FeiPriceUpdateSettings feiPriceUpdateSettings,
			  FeiPriceUpdateService feiPriceUpdateService) {
	    return taskBatchJobFactory.getStepBuilder("createCostUploadJob")
	        .<FeiPriceUpdateItem, PriceBookSync>chunk(1000)
	        .reader(tempDataFeiPriceUpdateDataReader())
	        .faultTolerant()
	        .processor(priceCalculationProcessor())
	        .writer(feiPricebookWriter(feiPriceUpdateService))
	        .build();
	  }
	
}
