package com.ferguson.cs.product.task.feipriceupdate;

import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.cs.product.task.feipriceupdate.batch.FeiBackupInputFileTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiCreateCostUpdateJobTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemProcessor;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemWriter;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateJobListener;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiCreatePriceUpdateTempTableTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiInputFileExistsDecider;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiInputFileProcessorListener;
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

	public FeiPriceUpdateTaskConfiguration(TaskBatchJobFactory taskBatchJobFactory,
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
	public FeiPriceUpdateItemProcessor priceUpdateItemprocessor(FeiPriceUpdateService feiPriceUpdateService) {
		return new FeiPriceUpdateItemProcessor(feiPriceUpdateService);
	}

	@Bean
	public Job feiUpdatePriceJob(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return taskBatchJobFactory.getJobBuilder("feiPriceUpdateJob")
				.listener(new FeiPriceUpdateJobListener(feiPriceUpdateSettings, feiPriceUpdateService))
				.start(createTempTableStep(feiPriceUpdateSettings, feiPriceUpdateService))
				.next(inputFileExistsDecider()).on(FeiInputFileExistsDecider.NO_INPUT_FILE).stop()
				.from(inputFileExistsDecider()).on(FeiInputFileExistsDecider.CONTINUE)
				.to(processInputFileStep(feiPriceUpdateSettings, feiPriceUpdateService))
				.next(createCostUploadJobTasklet(feiPriceUpdateSettings, feiPriceUpdateService))
				.next(backupInputFilesTasklet(feiPriceUpdateSettings)).end().build();
	}

	/*
	 * Decider that checks if an input file names was placed in the execution
	 * context by the parent step. If not then there was no input file and we will
	 * stop
	 */
	@Bean
	public FeiInputFileExistsDecider inputFileExistsDecider() {
		return new FeiInputFileExistsDecider();
	}
	
	/*
	 * Step 1
	 * Create the temp DB table Step
	 */
	@Bean
	public Step createTempTableStep(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return taskBatchJobFactory.getStepBuilder("createTempTableStep")
				.tasklet(feiPriceUpdateTempTableTasklet(feiPriceUpdateSettings, feiPriceUpdateService)).build();
	}

	/*
	 * Step 2
	 * Read the input price update CSV file and load the tempDB table in preparation
	 * for Cost Update job creation/execution
	 */
	@Bean
	public Step processInputFileStep(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return taskBatchJobFactory.getStepBuilder("processInputFile").listener(new FeiInputFileProcessorListener())
				.<FeiPriceUpdateItem, FeiPriceUpdateItem>chunk(1000).reader(allFilesReader()).faultTolerant()
				.processor(priceUpdateItemprocessor(feiPriceUpdateService))
				.writer(feiPriceUpdateItemWriter(feiPriceUpdateService)).build();
	}

	/*
	 * Step 3
	 * Step to create the job, load the data from the temp table (Done with a select into) and execute the job
	 */
	@Bean
	public Step createCostUploadJobTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return taskBatchJobFactory.getStepBuilder("createCostUploadJobTasklet")
				.tasklet(costUploadJobTasklet(feiPriceUpdateSettings, feiPriceUpdateService)).build();
	}

	/*
	 * Step 4
	 * move input file to backup folder
	 */
	@Bean
	public Step backupInputFilesTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings) {
		return taskBatchJobFactory.getStepBuilder("backupInputFilesTasklet")
				.tasklet(backupFileTasklet(feiPriceUpdateSettings)).build();
	}

	/*
	 * Job listener who's main purpose is to drop the temp table when complete
	 */
	@Bean
	@JobScope
	public FeiPriceUpdateJobListener feiPriceUpdateJobListener(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return new FeiPriceUpdateJobListener(feiPriceUpdateSettings, feiPriceUpdateService);
	}

	/*
	 * CSV input file reader
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<FeiPriceUpdateItem> feiPriceUpdateItemReader() {
		FlatFileItemReader<FeiPriceUpdateItem> reader = new FlatFileItemReader<>();
		reader.setLineMapper(feiPriceUpdateItemLineMapper());
		reader.setLinesToSkip(1);
		return reader;
	}

	/*
	 * We will process all files in the input folder if there a multiple
	 */
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

	/*
	 * Write the temp price update record to the temp table.  This step will also create a 2nd price update 
	 * record for the Pro pricing (PB22).  The input file represent customer pricing only (PB1)
	 */
	@Bean
	@StepScope
	public ItemWriter<FeiPriceUpdateItem> feiPriceUpdateItemWriter(FeiPriceUpdateService feiPriceUpdateService) {
		return new FeiPriceUpdateItemWriter(feiPriceUpdateService);
	}

	/*
	 * Tasklet to create the temp DB table.  Will make a call to drop table just in case it exists from
	 * a prior run
	 */
	@Bean
	@StepScope
	public FeiCreatePriceUpdateTempTableTasklet feiPriceUpdateTempTableTasklet(
			FeiPriceUpdateSettings feiPriceUpdateSettings, FeiPriceUpdateService feiPriceUpdateService) {
		return new FeiCreatePriceUpdateTempTableTasklet(feiPriceUpdateSettings, feiPriceUpdateService);
	}

	/*
	 * Tasklet to create the Cost upload Job, load the data from the temp table and execute the job
	 */
	@Bean
	@StepScope
	public FeiCreateCostUpdateJobTasklet costUploadJobTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		return new FeiCreateCostUpdateJobTasklet(feiPriceUpdateSettings, feiPriceUpdateService);
	}

	/*
	 * Tasklet to backup files
	 */
	@Bean
	@StepScope
	public FeiBackupInputFileTasklet backupFileTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings) {
		return new FeiBackupInputFileTasklet(feiPriceUpdateSettings);
	}

}
