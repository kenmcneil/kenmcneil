package com.ferguson.cs.product.task.feipricefeed;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.cs.product.task.feipricefeed.batch.CleanupStalePromoTasklet;
import com.ferguson.cs.product.task.feipricefeed.batch.ErrorFeiPriceDataFieldExtractor;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiFileSystemResource;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataClassifier;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataFieldExtractor;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataItemProcessor;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataJobListener;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataMapItemReader;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataMapItemWriter;
import com.ferguson.cs.product.task.feipricefeed.batch.SendErrorReportTasklet;
import com.ferguson.cs.product.task.feipricefeed.batch.SetItemReader;
import com.ferguson.cs.product.task.feipricefeed.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.utilities.DateUtils;

@Configuration
public class FeiPriceTaskConfiguration {
	private static final String FULL_FEED_NAME = "uploadFeiFullPriceFile";
	private static final String CHANGES_FEED_NAME = "uploadFeiPriceChangesFile";
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final SqlSessionFactory batchSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final FeiPriceService feiPriceService;
	private final FeiPriceSettings feiPriceSettings;

	public FeiPriceTaskConfiguration(@Qualifier("reporterSqlSessionFactory") SqlSessionFactory reporterSqlSessionFactory, @Qualifier("batchSqlSessionFactory") SqlSessionFactory batchSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory, FeiPriceService feiPriceService, FeiPriceSettings feiPriceSettings) {
		this.reporterSqlSessionFactory = reporterSqlSessionFactory;
		this.batchSqlSessionFactory = batchSqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.feiPriceService = feiPriceService;
		this.feiPriceSettings = feiPriceSettings;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<FeiPriceData> fullFeiPriceDataReader() {
		MyBatisCursorItemReader<FeiPriceData> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getFullFeiPriceData");
		reader.setSqlSessionFactory(reporterSqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<FeiPriceData> feiPriceChangeReader() {
		Date changesLastRun = feiPriceService.getLastRanDate(CHANGES_FEED_NAME);
		Date fullLastRun = feiPriceService.getLastRanDate(FULL_FEED_NAME);
		Date mostRecentRun;
		if (changesLastRun == null && fullLastRun != null) {
			mostRecentRun = fullLastRun;
		} else if (changesLastRun != null && fullLastRun == null) {
			mostRecentRun = changesLastRun;
		} else if (changesLastRun != null) {
			mostRecentRun = fullLastRun.compareTo(changesLastRun) > 0 ? fullLastRun : changesLastRun;
		} else {
			mostRecentRun = null;
		}
		MyBatisCursorItemReader<FeiPriceData> reader = new MyBatisCursorItemReader<>();
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("lastRanDate", mostRecentRun);
		reader.setParameterValues(parameters);
		reader.setQueryId("getFeiPriceChangesSinceLastRun");
		reader.setSqlSessionFactory(reporterSqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<Integer> promoProductReader() {
		MyBatisCursorItemReader<Integer> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getPromoFeiPriceProducts");
		reader.setSqlSessionFactory(reporterSqlSessionFactory);
		return reader;
	}


	@Bean
	@StepScope
	public MyBatisBatchItemWriter<FeiPriceData> feiPriceDataWhitelistWriter() {
		MyBatisBatchItemWriter<FeiPriceData> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(batchSqlSessionFactory);
		writer.setStatementId("updateFeiWhitelistPrice");
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public MyBatisBatchItemWriter<Integer> feiPromoPriceDataWhitelistWriter() {
		MyBatisBatchItemWriter<Integer> writer = new MyBatisBatchItemWriter<>();
		writer.setSqlSessionFactory(batchSqlSessionFactory);
		writer.setStatementId("insertFeiPromoWhitelistPrice");
		writer.setAssertUpdates(false);
		return writer;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<FeiPriceData> feiImapPriceDataReader() {
		MyBatisCursorItemReader<FeiPriceData> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getFeiImapPriceData");
		reader.setSqlSessionFactory(reporterSqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public MyBatisCursorItemReader<FeiPriceData> stalePromoPriceReader() {
		MyBatisCursorItemReader<FeiPriceData> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getStalePromoPriceProducts");
		reader.setSqlSessionFactory(reporterSqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public CompositeItemWriter<FeiPriceData> compositeFeiPriceDataWriter() {
		CompositeItemWriter<FeiPriceData> compositeItemWriter = new CompositeItemWriter<>();
		List<ItemWriter<? super FeiPriceData>> delegates = new ArrayList<>();
		for (String location : feiPriceSettings.getLocations().values()) {
			delegates.add(feiPriceDataWriter(location));
		}
		delegates.add(feiPriceDataWhitelistWriter());
		compositeItemWriter.setDelegates(delegates);

		return compositeItemWriter;
	}

	@Bean
	@StepScope
	public ClassifierCompositeItemWriter<FeiPriceData> feiPriceDataClassifierCompositeItemWriter(ItemStreamWriter<FeiPriceData> errorFeiPriceDataWriter) {
		ClassifierCompositeItemWriter<FeiPriceData> writer = new ClassifierCompositeItemWriter<>();
		writer.setClassifier(new FeiPriceDataClassifier(compositeFeiPriceDataWriter(), errorFeiPriceDataWriter));
		return writer;
	}

	@Bean
	@StepScope
	public MultiResourceItemReader<String> allFilesReader() {
		Resource[] resources;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		try {
			resources = patternResolver.getResources("file:" + feiPriceSettings.getTemporaryFilePath() + "*.csv");
		} catch (IOException e) {
			return null;
		}
		MultiResourceItemReader<String> reader = new MultiResourceItemReader<>();
		reader.setResources(resources);
		reader.setDelegate(new FlatFileItemReaderBuilder<String>().lineMapper(new PassThroughLineMapper())
				.name("allFilesReaderDelegate").build());
		return reader;
	}

	@Bean
	@StepScope
	public FeiPriceDataMapItemReader feiPriceDataMapItemReader(Map<String, List<FeiPriceData>> feiPriceDataMap) {
		return new FeiPriceDataMapItemReader(feiPriceDataMap);
	}

	@Bean
	@JobScope
	public FeiFileSystemResource feiFileSystemResource() {
		return new FeiFileSystemResource();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<String> aggregateWriter(FeiPriceService feiPriceService, FeiFileSystemResource feiFileSystemResource) {
		Integer runNumber = feiPriceService.getNumberOfRunsToday(CHANGES_FEED_NAME) + feiPriceService
				.getNumberOfRunsToday(FULL_FEED_NAME) + 1;
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyy_MM_dd");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("PM_%s_OMNI_%s.CSV", runNumber, dateString);
		feiFileSystemResource
				.setFileSystemResource(new FileSystemResource(feiPriceSettings.getStorageFilePath() + filename));
		return new FlatFileItemWriterBuilder<String>().resource(feiFileSystemResource.getFileSystemResource())
				.name("aggregateWriter")
				.lineAggregator(new PassThroughLineAggregator<>()).build();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<FeiPriceData> feiImapPriceDataWriter(FeiFileSystemResource feiFileSystemResource) {
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MMddyy");
		String dateString = DateUtils.dateToString(DateUtils.now(), dateTimeFormatter);
		String filename = String.format("build_imap_pricing_%s.csv", dateString);
		String[] names = new String[]{"uniqueId", "mpid", "price"};
		feiFileSystemResource
				.setFileSystemResource(new FileSystemResource(feiPriceSettings.getImapFilePath() + filename));
		DelimitedLineAggregator<FeiPriceData> lineAggregator = new DelimitedLineAggregator<>();
		return new FlatFileItemWriterBuilder<FeiPriceData>().resource(feiFileSystemResource.getFileSystemResource())
				.name("feiImapPriceDataWriter")
				.delimited().names(names).headerCallback(writer -> writer.write(String.join(",", names))).build();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<FeiPriceData> errorFeiPriceDataWriter(@Value("#{stepExecution.jobExecution}") JobExecution jobExecution) {
		String[] columnNames = new String[]{"uniqueId", "mpid", "price", "brand", "product status", "error reason"};
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MMddyy");
		String dateString = DateUtils.dateToString(DateUtils.now(), dateTimeFormatter);
		String filePath = String
				.format("%s" + "outbound_fei_errors_%s.csv", feiPriceSettings.getStorageFilePath(), dateString);
		jobExecution.getExecutionContext().putString("errorReport", filePath);

		return new FlatFileItemWriterBuilder<FeiPriceData>().delimited().delimiter(",")
				.fieldExtractor(new ErrorFeiPriceDataFieldExtractor())
				.headerCallback(p -> p
						.write(String.join(",", columnNames))).name("errorFeiPriceDataWriter")
				.resource(new FileSystemResource(filePath)).shouldDeleteIfEmpty(true).build();
	}

	@Bean
	@JobScope
	public Map<String, List<FeiPriceData>> feiPriceDataMap() {
		return new HashMap<>();
	}

	@Bean
	@JobScope
	public Set<FeiPriceData> duplicateData() {
		return new HashSet<>();
	}

	@Bean
	@StepScope
	public FeiPriceDataMapItemWriter feiPriceDataMapItemWriter(Map<String, List<FeiPriceData>> feiPriceDataMap) {
		return new FeiPriceDataMapItemWriter(feiPriceDataMap, feiPriceService);
	}

	@Bean
	@StepScope
	public SetItemReader<FeiPriceData> duplicateDataReader() {
		return new SetItemReader<>(duplicateData());
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<FeiPriceData> duplicateDataWriter() {
		String[] fields = new String[]{"uniqueId", "mpid", "price", "brand", "status"};

		return new FlatFileItemWriterBuilder<FeiPriceData>().delimited().delimiter(",").names(fields)
				.headerCallback(p -> p
						.write(String.join(",", fields))).name("duplicateDataWriter")
				.resource(new FileSystemResource(feiPriceSettings.getStorageFilePath() + "duplicate_mpns.csv")).build();
	}

	@Bean
	@StepScope
	public CleanupStalePromoTasklet cleanupStalePromoTasklet(FeiPriceService feiPriceService) {
		return new CleanupStalePromoTasklet(feiPriceService);
	}

	@Bean
	@StepScope
	public SendErrorReportTasklet sendErrorReportTasklet(FeiPriceSettings feiPriceSettings, BuildWebServicesFeignClient buildWebServicesFeignClient) {
		return new SendErrorReportTasklet(feiPriceSettings, buildWebServicesFeignClient);
	}

	@Bean
	@StepScope
	public FeiPriceDataItemProcessor feiPriceDataItemProcessor(FeiPriceService feiPriceService) {
		return new FeiPriceDataItemProcessor(feiPriceService, feiPriceSettings);
	}

	@Bean
	public Step writeFullPriceDataToMap(FeiPriceDataMapItemWriter feiPriceDataMapItemWriter, MyBatisCursorItemReader<FeiPriceData> fullFeiPriceDataReader) {
		return taskBatchJobFactory.getStepBuilder("writeFullPriceDataToMap").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(fullFeiPriceDataReader).writer(feiPriceDataMapItemWriter).build();
	}

	@Bean
	public Step writeLocationPriceDataToFiles(FeiPriceDataMapItemReader feiPriceDataMapItemReader,
											  ClassifierCompositeItemWriter<FeiPriceData> feiPriceDataClassifierCompositeItemWriter,
											  FeiPriceDataItemProcessor feiPriceDataItemProcessor,
											  ItemStreamWriter<FeiPriceData> compositeFeiPriceDataWriter,
											  ItemStreamWriter<FeiPriceData> errorFeiPriceDataWriter) {
		return taskBatchJobFactory
				.getStepBuilder("writeLocationPriceDataToFiles").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(feiPriceDataMapItemReader).processor(feiPriceDataItemProcessor)
				.writer(feiPriceDataClassifierCompositeItemWriter).stream(compositeFeiPriceDataWriter)
				.stream(errorFeiPriceDataWriter).build();
	}

	@Bean
	public Step writePriceDataChangesToMap(FeiPriceDataMapItemWriter feiPriceDataMapItemWriter, MyBatisCursorItemReader<FeiPriceData> feiPriceChangeReader) {
		return taskBatchJobFactory.getStepBuilder("writePriceDataChangesToMap").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(feiPriceChangeReader).writer(feiPriceDataMapItemWriter).build();
	}

	@Bean
	public Step combineLocationPriceFiles(MultiResourceItemReader<String> allFilesReader, FlatFileItemWriter<String> aggregateWriter) {
		return taskBatchJobFactory.getStepBuilder("combineLocationPriceFiles").<String, String>chunk(100000)
				.reader(allFilesReader).writer(aggregateWriter).build();
	}

	@Bean
	public Step writeDuplicateMpns() {
		return taskBatchJobFactory.getStepBuilder("writeDuplicateMpns").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(duplicateDataReader()).writer(duplicateDataWriter()).build();
	}

	@Bean
	public Step writeFeiImapPriceData(FlatFileItemWriter<FeiPriceData> feiImapPriceDataWriter) {
		return taskBatchJobFactory.getStepBuilder("writeFeiImapPriceData").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(feiImapPriceDataReader()).writer(feiImapPriceDataWriter).build();
	}

	@Bean
	public Step addPromoProductsToFeiWhitelist(MyBatisCursorItemReader<Integer> promoProductReader, MyBatisBatchItemWriter<Integer> feiPromoPriceDataWhitelistWriter) {
		return taskBatchJobFactory.getStepBuilder("addPromoProductsToFeiWhitelist").<Integer, Integer>chunk(1000)
				.reader(promoProductReader).writer(feiPromoPriceDataWhitelistWriter).build();
	}

	@Bean
	public Step cleanupStalePromoProducts(CleanupStalePromoTasklet cleanupStalePromoTasklet) {
		return taskBatchJobFactory.getStepBuilder("cleanupStalePromoProducts").tasklet(cleanupStalePromoTasklet)
				.build();
	}

	@Bean
	public Step markStalePromoProducts(MyBatisCursorItemReader<FeiPriceData> stalePromoPriceReader, MyBatisBatchItemWriter<FeiPriceData> feiPriceDataWhitelistWriter) {
		return taskBatchJobFactory.getStepBuilder("markStalePromoProducts").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(stalePromoPriceReader).writer(feiPriceDataWhitelistWriter).build();
	}

	@Bean
	public Step sendErrorReport(SendErrorReportTasklet sendErrorReportTasklet) {
		return taskBatchJobFactory.getStepBuilder("sendErrorReport").tasklet(sendErrorReportTasklet).build();
	}

	@Bean
	public Job uploadFeiFullPriceFile(Step writeFullPriceDataToMap, Step writeLocationPriceDataToFiles, Step combineLocationPriceFiles, Step addPromoProductsToFeiWhitelist, Step cleanupStalePromoProducts, Step sendErrorReport) {
		return taskBatchJobFactory.getJobBuilder("uploadFeiFullPriceFile").listener(feiPriceDataJobListener())
				.start(addPromoProductsToFeiWhitelist).next(writeFullPriceDataToMap).next(writeLocationPriceDataToFiles)
				.next(combineLocationPriceFiles).next(cleanupStalePromoProducts).next(sendErrorReport).build();

	}

	@Bean
	public Job uploadFeiPriceChangesFile(Step writePriceDataChangesToMap, Step writeLocationPriceDataToFiles, Step combineLocationPriceFiles, Step addPromoProductsToFeiWhitelist, Step cleanupStalePromoProducts, Step sendErrorReport) {
		return taskBatchJobFactory.getJobBuilder("uploadFeiPriceChangesFile").listener(feiPriceDataJobListener())
				.start(addPromoProductsToFeiWhitelist).next(writePriceDataChangesToMap)
				.next(writeLocationPriceDataToFiles)
				.next(combineLocationPriceFiles).next(cleanupStalePromoProducts).next(sendErrorReport)
				.build();
	}

	@Bean
	public Job uploadFeiImapPriceFile(Step writeFeiImapPriceData) {
		return taskBatchJobFactory.getJobBuilder("uploadFeiImapPriceFile").start(writeFeiImapPriceData).build();
	}

	@Bean
	@JobScope
	public FeiPriceDataJobListener feiPriceDataJobListener() {
		return new FeiPriceDataJobListener(feiPriceService, feiPriceSettings);
	}

	private FlatFileItemWriter<FeiPriceData> feiPriceDataWriter(String location) {
		FieldExtractor<FeiPriceData> fieldExtractor = new FeiPriceDataFieldExtractor(location);
		String fileName = "temp" + location.replace("*", "") + UUID.randomUUID().toString().substring(0, 8) + ".csv";
		return new FlatFileItemWriterBuilder<FeiPriceData>().delimited().delimiter(",").fieldExtractor(fieldExtractor)
				.name(StringUtils.capitalize(location) + "PriceDataWriter")
				.resource(new FileSystemResource(feiPriceSettings.getTemporaryFilePath() + fileName)).build();
	}
}
