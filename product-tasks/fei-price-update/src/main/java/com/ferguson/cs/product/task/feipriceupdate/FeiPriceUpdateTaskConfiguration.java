package com.ferguson.cs.product.task.feipriceupdate;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.cs.product.task.feipriceupdate.batch.FeiBackupInputFileTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiCreateCostUpdateJobTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiCreatePriceUpdateTempTableTasklet;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiInputFileExistsDecider;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiInputFileProcessorListener;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateFileSystemResource;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemProcessor;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateItemWriter;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiPriceUpdateJobListener;
import com.ferguson.cs.product.task.feipriceupdate.batch.FeiSendErrorReportTasklet;
import com.ferguson.cs.product.task.feipriceupdate.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PricebookType;
import com.ferguson.cs.product.task.feipriceupdate.notification.NotificationService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.utilities.DateUtils;

@Configuration
@EnableFeignClients
public class FeiPriceUpdateTaskConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateTaskConfiguration.class);

	private final TaskBatchJobFactory taskBatchJobFactory;
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final NotificationService notificationService;
	private final SqlSessionFactory sqlSessionFactory;
	private final FeiPriceUpdateService feiPriceUpdateService;
	private final BuildWebServicesFeignClient buildWebServicesFeignClient;

	public FeiPriceUpdateTaskConfiguration(
			TaskBatchJobFactory taskBatchJobFactory,
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			NotificationService notificationService,
			SqlSessionFactory sqlSessionFactory,
			FeiPriceUpdateService feiPriceUpdateService,
			BuildWebServicesFeignClient buildWebServicesFeignClient) {
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.notificationService = notificationService;
		this.sqlSessionFactory = sqlSessionFactory;
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.buildWebServicesFeignClient = buildWebServicesFeignClient;
	}

	/*
	 * Job
	 */
	@Bean
	public Job feiUpdatePriceJob(
			Step createTempTableStep,
			Step createPriceUpdateErrorReport,
			Step createCostUploadJobTasklet,
			Step emailErrorReportTasklet,
			Step backupInputFilesTasklet,
			Flow validateInputFilesExistFlow,
			Flow pb1FileProcessorFlow,
			Flow pb22FileProcessorFlow) {
		return taskBatchJobFactory.getJobBuilder("feiPriceUpdateJob")
				.listener(new FeiPriceUpdateJobListener(feiPriceUpdateSettings, feiPriceUpdateService,notificationService))
				.flow(createTempTableStep)
				.next(validateInputFilesExistFlow)
				.next(pb1FileProcessorFlow)
				.next(pb22FileProcessorFlow)
				.next(createCostUploadJobTasklet)
				.next(createPriceUpdateErrorReport)
				.next(emailErrorReportTasklet)
				.next(backupInputFilesTasklet).end().build();
	}


	/*
	 * Step 1
	 * Create the temporary DB table Step, update job execution context with data required downstream
	 */
	@Bean
	public Step createTempTableStep() {
		return taskBatchJobFactory.getStepBuilder("createTempTableStep")
				.tasklet(feiPriceUpdateTempTableTasklet()).build();
	}

	/*
	 * Check if input files exist in FTP folder.  If not we stop.  Nothing to do.
	 */
	@Bean
	public Flow validateInputFilesExistFlow() {
		return new FlowBuilder<Flow>("validateInputFilesExistFlow")
				.start(inputFileExistsDecider()).on(FeiInputFileExistsDecider.NO_INPUT_FILE).stop()
				.from(inputFileExistsDecider()).on(FeiInputFileExistsDecider.CONTINUE).end("COMPLETED")
				.build();
	}

	/*
	 * Check if PB1 files exists and if so call the PB1 processing step
	 */
	@Bean
	public Flow pb1FileProcessorFlow() {
		return new FlowBuilder<Flow>("pb1FileProcessorFlow")
				.start(pb1inputFileExistsDecider()).on(FeiInputFileExistsDecider.CONTINUE).to(processPb1InputFileStep())
				.from(pb1inputFileExistsDecider()).on(FeiInputFileExistsDecider.NO_INPUT_FILE).end("COMPLETED")
				.build();
	}

	/*
	 * Check if the PB22 files exists and if so call the PB22 processing step
	 */
	@Bean
	public Flow pb22FileProcessorFlow() {
		return new FlowBuilder<Flow>("pb22FileProcessorFlow")
				.start(pb22inputFileExistsDecider()).on(FeiInputFileExistsDecider.CONTINUE).to(processPb22InputFileStep())
				.from(pb22inputFileExistsDecider()).on(FeiInputFileExistsDecider.NO_INPUT_FILE).end("COMPLETED")
				.build();
	}

	/*
	 * Step 2
	 * Conditional, executed only if PB1 file exists
	 * Read the PB1 input price update CSV file and load the tempDB table in preparation
	 * for Cost Update job creation/execution.
	 */
	@Bean
	public Step processPb1InputFileStep() {
		return taskBatchJobFactory.getStepBuilder("processPb1InputFile").listener(new FeiInputFileProcessorListener(PricebookType.PB1, notificationService))
				.<FeiPriceUpdateItem, FeiPriceUpdateItem>chunk(1000).reader(readPb1File(null)).faultTolerant()
				.processor(pb1PriceUpdateItemprocessor())
				.writer(feiPriceUpdateItemWriter()).build();
	}

	/*
	 * Step 3
	 * Conditional, Executed only if the PB22 file exists
	 * Read the PB22 input price update CSV file and load the tempDB table in preparation
	 * for Cost Update job creation/execution
	 */
	@Bean
	public Step processPb22InputFileStep() {
		return taskBatchJobFactory.getStepBuilder("processPb22InputFile").listener(new FeiInputFileProcessorListener(PricebookType.PB22, notificationService))
				.<FeiPriceUpdateItem, FeiPriceUpdateItem>chunk(1000).reader(readPb22File(null)).faultTolerant()
				.processor(pb22PriceUpdateItemprocessor())
				.writer(feiPriceUpdateItemWriter()).build();
	}

	/*
	 * Step 4
	 * Step to create the Cost Uploader job, load job data from temp
	 * table (Done with a select into) and execute the Cost Uploader job
	 */
	@Bean
	public Step createCostUploadJobTasklet() {
		return taskBatchJobFactory.getStepBuilder("createCostUploadJobTasklet")
				.tasklet(costUploadJobTasklet()).build();
	}

	/*
	 * Step 5
	 * Step to write any update validation errors to a csv file to be emailed out.
	 */
	@Bean
	public Step createPriceUpdateErrorReport(FlatFileItemWriter<FeiPriceUpdateItem> feiPriceUpdateErrorReportWriter,
			MyBatisCursorItemReader<FeiPriceUpdateItem> feiPriceUpdateErrorDataReader) {
		return taskBatchJobFactory.getStepBuilder("writeFeiPriceUpdateErrorData").<FeiPriceUpdateItem, FeiPriceUpdateItem>chunk(1000)
				.reader(feiPriceUpdateErrorDataReader).writer(feiPriceUpdateErrorReportWriter).build();
	}

	/*
	 * Step 6
	 * Email error report created in step 5
	 */
	@Bean
	public Step emailErrorReportTasklet() {
		return taskBatchJobFactory.getStepBuilder("emailErrorReport")
				.tasklet(sendErrorReportTasklet()).build();
	}

	/*
	 * Step 7
	 * move input file to backup folder
	 */
	@Bean
	public Step backupInputFilesTasklet() {
		return taskBatchJobFactory.getStepBuilder("backupInputFilesTasklet")
				.tasklet(backupFileTasklet()).build();
	}

	/*
	 * Decider that checks if we have any input files to process.
	 */
	@Bean
	public FeiInputFileExistsDecider inputFileExistsDecider() {
		return new FeiInputFileExistsDecider(null);
	}

	/*
	 * Decider that checks if we have a PB1 file to process.
	 */
	@Bean
	public FeiInputFileExistsDecider pb1inputFileExistsDecider() {
		return new FeiInputFileExistsDecider(PricebookType.PB1);
	}

	/*
	 * Decider that checks if we have a PB22 file to process.
	 */
	@Bean
	public FeiInputFileExistsDecider pb22inputFileExistsDecider() {
		return new FeiInputFileExistsDecider(PricebookType.PB22);
	}

	/*
	 * Job listener who's main purpose is to drop the temp table when complete
	 */
	@Bean
	@JobScope
	public FeiPriceUpdateJobListener feiPriceUpdateJobListener() {
		return new FeiPriceUpdateJobListener(feiPriceUpdateSettings, feiPriceUpdateService,notificationService);
	}

	/*
	 * Line mapper.  Both input files are in the same format so this is used for both.
	 */
	@Bean
	public LineMapper<FeiPriceUpdateItem> feiPriceUpdateItemLineMapper() {
		DefaultLineMapper<FeiPriceUpdateItem> lineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
		lineTokenizer.setNames("mpid", "uniqueId", "price");
		BeanWrapperFieldSetMapper<FeiPriceUpdateItem> mapper = new BeanWrapperFieldSetMapper<>();
		mapper.setTargetType(FeiPriceUpdateItem.class);
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(mapper);
		return lineMapper;
	}

	/*
	 * CSV input file reader
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<FeiPriceUpdateItem> feiPriceUpdateItemReader() {
		FlatFileItemReader<FeiPriceUpdateItem> reader = new FlatFileItemReader<>();
		reader.setLineMapper(feiPriceUpdateItemLineMapper());
		reader.setLinesToSkip(0);
		return reader;
	}

	/*
	 * Read PB1 file (fei_show_nets)
	 */
	@Bean
	@StepScope
	public MultiResourceItemReader<FeiPriceUpdateItem> readPb1File(@Value("#{jobExecutionContext['PB1_inputFile']}") String fileName) {
		Resource[] resources = null;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		try {
			resources = patternResolver.getResources("file:" + feiPriceUpdateSettings.getInputFilePath() + fileName);
		} catch (IOException e) {
			LOGGER.error("Error processing input CSV file(s). Exception: {}", e.toString());
		}

		MultiResourceItemReader<FeiPriceUpdateItem> resourceItemReader = new MultiResourceItemReader<FeiPriceUpdateItem>();
		resourceItemReader.setResources(resources);
		resourceItemReader.setDelegate(feiPriceUpdateItemReader());
		return resourceItemReader;
	}

	/*
	 * Read PB22 file (fei_pro_nets)
	 */
	@Bean
	@StepScope
	public MultiResourceItemReader<FeiPriceUpdateItem> readPb22File(@Value("#{jobExecutionContext['PB22_inputFile']}") String fileName) {
		Resource[] resources = null;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		try {
			resources = patternResolver.getResources("file:" + feiPriceUpdateSettings.getInputFilePath() + fileName);
		} catch (IOException e) {
			LOGGER.error("Error processing input CSV file(s). Exception: {}", e.toString());
		}

		MultiResourceItemReader<FeiPriceUpdateItem> resourceItemReader = new MultiResourceItemReader<FeiPriceUpdateItem>();
		resourceItemReader.setResources(resources);
		resourceItemReader.setDelegate(feiPriceUpdateItemReader());
		return resourceItemReader;
	}

	/*
	 * PB 1 processor
	 */
	@Bean
	public FeiPriceUpdateItemProcessor pb1PriceUpdateItemprocessor() {
		return new FeiPriceUpdateItemProcessor(PricebookType.PB1, feiPriceUpdateService, feiPriceUpdateSettings);
	}

	/*
	 * PB22 processor
	 */
	@Bean
	public FeiPriceUpdateItemProcessor pb22PriceUpdateItemprocessor() {
		return new FeiPriceUpdateItemProcessor(PricebookType.PB22, feiPriceUpdateService, feiPriceUpdateSettings);
	}

	/*
	 * Writer
	 * Used for both input files
	 */
	@Bean
	@StepScope
	public ItemWriter<FeiPriceUpdateItem> feiPriceUpdateItemWriter() {
		return new FeiPriceUpdateItemWriter(feiPriceUpdateService);
	}

	/*
	 * CSV error report reader.  Will extract all records from the temp DB table where the
	 * updateStatusId is not zero, indicating some sort of error during record processing validation
	 */
	@Bean
	@StepScope
	public MyBatisCursorItemReader<FeiPriceUpdateItem> feiPriceUpdateErrorDataReader() {
		MyBatisCursorItemReader<FeiPriceUpdateItem> reader = new MyBatisCursorItemReader<>();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("tempTableName", feiPriceUpdateSettings.getTempTableName());
		reader.setParameterValues(parameters);
		reader.setQueryId("getFeiPriceUpdateErrors");
		reader.setSqlSessionFactory(sqlSessionFactory);
		return reader;
	}

	/*
	 * CSV error report writer
	 */
	@Bean
	@StepScope
	public FlatFileItemWriter<FeiPriceUpdateItem> feiPriceUpdateErrorReportWriter(
			@Value("#{stepExecution.jobExecution}") JobExecution jobExecution,
			FeiPriceUpdateFileSystemResource feiFileSystemResource) {
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyyMMdd_HHmmss");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("build_fei_price_update_error_report_%s.csv", dateString );
		String[] names = new String[]{"uniqueId", "mpid", "pricebookId", "price", "StatusMsg"};

		// Stick this in execution context.  Will check in error report email step to see if it exists and if so it will
		// get sent out.  If no error records then the file won't be created.
		jobExecution.getExecutionContext().put("ERROR_REPORT",feiPriceUpdateSettings.getBackupFolderPath() + filename);

		feiFileSystemResource.setFileSystemResource(
				new FileSystemResource(feiPriceUpdateSettings.getBackupFolderPath() + filename));

		return new FlatFileItemWriterBuilder<FeiPriceUpdateItem>().resource(feiFileSystemResource.getFileSystemResource())
				.name("feiPriceUpdateErrorReportWriter")
				.shouldDeleteIfEmpty(true)
				.delimited().names(names)
				.headerCallback(writer -> writer.write(String.join(",", names))).build();
	}

	/*
	 * Tasklet to create the temp DB table.  Will make a call to drop table just in case it exists from
	 * a prior run
	 */
	@Bean
	public FeiCreatePriceUpdateTempTableTasklet feiPriceUpdateTempTableTasklet() {
		return new FeiCreatePriceUpdateTempTableTasklet(feiPriceUpdateSettings, feiPriceUpdateService, notificationService);
	}

	/*
	 * Tasklet to create the Cost upload Job, load the data from the temp table and execute the job
	 */
	@Bean
	public FeiCreateCostUpdateJobTasklet costUploadJobTasklet() {
		return new FeiCreateCostUpdateJobTasklet(feiPriceUpdateSettings, feiPriceUpdateService, notificationService);
	}

	/*
	 * Tasklet to email the error report
	 */
	@Bean
	public FeiSendErrorReportTasklet sendErrorReportTasklet() {
		return new FeiSendErrorReportTasklet(feiPriceUpdateSettings,buildWebServicesFeignClient);
	}

	/*
	 * Tasklet to backup files
	 */
	@Bean
	public FeiBackupInputFileTasklet backupFileTasklet() {
		return new FeiBackupInputFileTasklet(feiPriceUpdateSettings);
	}

	@Bean
	@JobScope
	public FeiPriceUpdateFileSystemResource feiFileSystemResource() {
		return new FeiPriceUpdateFileSystemResource();
	}

}
