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
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.cs.product.task.feipricefeed.batch.FeiFileSystemResource;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataFieldExtractor;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataJobListener;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataMapItemReader;
import com.ferguson.cs.product.task.feipricefeed.batch.FeiPriceDataMapItemWriter;
import com.ferguson.cs.product.task.feipricefeed.batch.SetItemReader;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.utilities.DateUtils;

@Configuration
public class FeiPriceTaskConfiguration {
	private static final String FULL_FEED_NAME = "uploadFeiFullPriceFile";
	private static final String CHANGES_FEED_NAME = "uploadFeiPriceChangesFile";
	private final SqlSessionFactory reporterSqlSessionFactory;
	private final TaskBatchJobFactory taskBatchJobFactory;
	private final FeiPriceService feiPriceService;
	private final FeiPriceSettings feiPriceSettings;

	public FeiPriceTaskConfiguration(SqlSessionFactory reporterSqlSessionFactory, TaskBatchJobFactory taskBatchJobFactory, FeiPriceService feiPriceService, FeiPriceSettings feiPriceSettings) {
		this.reporterSqlSessionFactory = reporterSqlSessionFactory;
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
	public MyBatisCursorItemReader<FeiPriceData> feiImapPriceDataReader() {
		MyBatisCursorItemReader<FeiPriceData> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getFeiImapPriceData");
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
		compositeItemWriter.setDelegates(delegates);

		return compositeItemWriter;
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
	public FeiPriceDataMapItemReader feiPriceDataMapItemReader(Map<String, FeiPriceData> feiPriceDataMap) {
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
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MMddyy");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
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
	@JobScope
	public Map<String, FeiPriceData> feiPriceDataMap() {
		return new HashMap<>();
	}

	@Bean
	@JobScope
	public Set<FeiPriceData> duplicateData() {
		return new HashSet<>();
	}

	@Bean
	@StepScope
	public FeiPriceDataMapItemWriter feiPriceDataMapItemWriter(Map<String,FeiPriceData> feiPriceDataMap) {
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
		String[] fields = new String[]{"uniqueId", "mpn", "price", "brand", "status"};

		return new FlatFileItemWriterBuilder<FeiPriceData>().delimited().delimiter(",").names(fields)
				.headerCallback(p -> p
						.write(String.join(",", fields))).name("duplicateDataWriter")
				.resource(new FileSystemResource(feiPriceSettings.getStorageFilePath() + "duplicate_mpns.csv")).build();
	}

	@Bean
	public Step writeFullPriceDataToMap(FeiPriceDataMapItemWriter feiPriceDataMapItemWriter, MyBatisCursorItemReader<FeiPriceData> fullFeiPriceDataReader) {
		return taskBatchJobFactory.getStepBuilder("writeFullPriceDataToMap").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(fullFeiPriceDataReader).writer(feiPriceDataMapItemWriter).build();
	}

	@Bean
	public Step writeLocationPriceDataToFiles(FeiPriceDataMapItemReader feiPriceDataMapItemReader, CompositeItemWriter<FeiPriceData> compositeFeiPriceDataWriter) {
		return taskBatchJobFactory
				.getStepBuilder("writeLocationPriceDataToFiles").<FeiPriceData, FeiPriceData>chunk(1000)
				.reader(feiPriceDataMapItemReader).writer(compositeFeiPriceDataWriter).build();
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
	public Job uploadFeiFullPriceFile(Step writeFullPriceDataToMap, Step writeLocationPriceDataToFiles, Step combineLocationPriceFiles) {
		return taskBatchJobFactory.getJobBuilder("uploadFeiFullPriceFile").listener(feiPriceDataJobListener())
				.start(writeFullPriceDataToMap).next(writeLocationPriceDataToFiles).next(combineLocationPriceFiles)
				.next(writeDuplicateMpns()).build();

	}

	@Bean
	public Job uploadFeiPriceChangesFile(Step writePriceDataChangesToMap, Step writeLocationPriceDataToFiles, Step combineLocationPriceFiles) {
		return taskBatchJobFactory.getJobBuilder("uploadFeiPriceChangesFile").listener(feiPriceDataJobListener())
				.start(writePriceDataChangesToMap).next(writeLocationPriceDataToFiles).next(combineLocationPriceFiles)
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
