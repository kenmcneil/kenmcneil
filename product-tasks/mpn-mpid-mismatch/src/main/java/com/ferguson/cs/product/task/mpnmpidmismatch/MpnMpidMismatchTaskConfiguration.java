package com.ferguson.cs.product.task.mpnmpidmismatch;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchEmailReportTasklet;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchFileSystemResource;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchInsertMissingMpidTasklet;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchItemProcessor;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchMpidBackfillDecider;
import com.ferguson.cs.product.task.mpnmpidmismatch.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.mpnmpidmismatch.client.PdmMdmWebServicesFeignClient;
import com.ferguson.cs.product.task.mpnmpidmismatch.data.MpnMpidMismatchService;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.MpnMpidProductItem;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;
import com.ferguson.cs.utilities.DateUtils;

@Configuration
@EnableConfigurationProperties(MpnMpidMismatchSettings.class)
public class MpnMpidMismatchTaskConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(MpnMpidMismatchTaskConfiguration.class);

	private final TaskBatchJobFactory taskBatchJobFactory;
	private final SqlSessionFactory sqlSessionFactory;
	private final MpnMpidMismatchSettings mpnMpidMismatchSettings;
	private final BuildWebServicesFeignClient buildWebServicesFeignClient;
	private final PdmMdmWebServicesFeignClient pdmMdmWebServicesFeignClient;
	private final MpnMpidMismatchService mpnMpidMismatchService;

	public MpnMpidMismatchTaskConfiguration(
			TaskBatchJobFactory taskBatchJobFactory,
			SqlSessionFactory sqlSessionFactory,
			MpnMpidMismatchSettings mpnMpidMismatchSettings,
			BuildWebServicesFeignClient buildWebServicesFeignClient,
			MpnMpidMismatchService mpnMpidMismatchService,
			PdmMdmWebServicesFeignClient pdmMdmWebServicesFeignClient) {
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.sqlSessionFactory = sqlSessionFactory;
		this.mpnMpidMismatchSettings = mpnMpidMismatchSettings;
		this.buildWebServicesFeignClient = buildWebServicesFeignClient;
		this.mpnMpidMismatchService = mpnMpidMismatchService;
		this.pdmMdmWebServicesFeignClient = pdmMdmWebServicesFeignClient;
	}

	@Bean
	public Job mpnMpidMismatchJob(
			Step  createMpnMpidMissingReport,
			Step missingMpidInsertTasklet,
			Step createMpnMpidMismatchReport,
			Step emailReportTasklet) {
		return taskBatchJobFactory.getJobBuilder("mpnMpidMismatchJob")
				.start(createMpnMpidMissingReport)
				.next(mpnMpidMismatchMpidBackfillDecider()).on(MpnMpidMismatchMpidBackfillDecider.NO_MISSING_RECORDS)
				.to(createMpnMpidMismatchReport)
				.from(mpnMpidMismatchMpidBackfillDecider()).on(MpnMpidMismatchMpidBackfillDecider.CONTINUE)
				.to(missingMpidInsertTasklet)
				.next(createMpnMpidMismatchReport)
				.next(emailReportTasklet)
				.end().build();
	}

	/*
	 * Step 1
	 * Create report with all the missing feiMPID records.  These will be inserted in the next
	 * step if some exist
	 */
	@Bean
	public Step createMpnMpidMissingReport(
			MyBatisCursorItemReader<MpnMpidProductItem> mpmMpidMissingItemReader,
			//MpnMpidMismatchItemProcessor mpnMpidMismatchItemProcessor,
			FlatFileItemWriter<MpnMpidProductItem> mpmMpidMissingReportWriter) {
		return taskBatchJobFactory.getStepBuilder("writeMpnMpidMissinghData").<MpnMpidProductItem, MpnMpidProductItem>chunk(1000)
				.reader(mpmMpidMissingItemReader).writer(mpmMpidMissingReportWriter).build();
	}

	/*
	 * Step 2
	 * Tasklet to populate the feiMPID table with missing records based on vendor_mapping mpn's
	 */
	@Bean
	public Step missingMpidInsertTasklet() {
		return taskBatchJobFactory.getStepBuilder("missingMpidInsertTasklet")
				.tasklet(insertMissingMpidTasklet()).build();
	}

	/*
	 * Step 3
	 * Create report with all the mismatch MPN/MPID's.  Will need to reach out to MDM for additional details
	 * to facilitate resolution
	 */
	@Bean
	public Step createMpnMpidMismatchReport(
			MyBatisCursorItemReader<MpnMpidProductItem> mpmMpidMismatchItemReader,
			MpnMpidMismatchItemProcessor mpnMpidMismatchItemProcessor,
			FlatFileItemWriter<MpnMpidProductItem> mpmMpidMismatchReportWriter) {
		return taskBatchJobFactory.getStepBuilder("writeMpnMpidMismatchData").<MpnMpidProductItem, MpnMpidProductItem>chunk(1000)
				.reader(mpmMpidMismatchItemReader).processor(mpnMpidMismatchItemProcessor).writer(mpmMpidMismatchReportWriter).build();
	}

	@Bean
	public Step emailReportTasklet() {
		return taskBatchJobFactory.getStepBuilder("emailReportTasklet")
				.tasklet(mpnMpidMismatchSendReportTasklet()).build();
	}

	/*
	 * Mybatis reader - Will return all records that have a product.uniqueId but no corresponding
	 * match in the feuMPID table
	 */
	@Bean
	@StepScope
	public MyBatisCursorItemReader<MpnMpidProductItem> mpmMpidMissingItemReader() {
		MyBatisCursorItemReader<MpnMpidProductItem> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getMpnMpidMissingItems");
		reader.setSqlSessionFactory(sqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<MpnMpidProductItem> mpmMpidMissingReportWriter(
			MpnMpidMismatchFileSystemResource mpnMpidMismatchFileSystemResource) {
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyyMMdd_HHmmss");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("%s_%s.csv", mpnMpidMismatchSettings.getMissingReportFilenamePrefix(), dateString );
		String[] names = new String[]{"uniqueId", "productId", "manufacturer", "finish", "upc", "sku","mpn"};

		String slash = mpnMpidMismatchSettings.getReportOutputFolder().endsWith("/") ? "" : "/";

		mpnMpidMismatchFileSystemResource.setFileSystemResource(
				new FileSystemResource(mpnMpidMismatchSettings.getReportOutputFolder() + slash + filename));

		return new FlatFileItemWriterBuilder<MpnMpidProductItem>().resource(mpnMpidMismatchFileSystemResource.getFileSystemResource())
				.name("mpmMpidMismatchReportWriter")
				.shouldDeleteIfEmpty(true)
				.delimited().names(names)
				.headerCallback(writer -> writer.write(String.join(",", names))).build();
	}

	/*
	 * Mybatis reader - Will return all records with the same product uniqueID but mismatch between vendor_mapping.mpn
	 * and feiMPID.mpiid
	 */
	@Bean
	@StepScope
	public MyBatisCursorItemReader<MpnMpidProductItem> mpmMpidMismatchItemReader() {
		MyBatisCursorItemReader<MpnMpidProductItem> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getMpnMpidMismatchItems");
		reader.setSqlSessionFactory(sqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<MpnMpidProductItem> mpmMpidMismatchReportWriter(
			MpnMpidMismatchFileSystemResource mpnMpidMismatchFileSystemResource) {
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyyMMdd_HHmmss");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("%s_%s.csv", mpnMpidMismatchSettings.getMismatchReportFilenamePrefix(), dateString );
		String[] names = new String[]{"uniqueId","mpn","mpid"};

		String slash = mpnMpidMismatchSettings.getReportOutputFolder().endsWith("/") ? "" : "/";

		mpnMpidMismatchFileSystemResource.setFileSystemResource(
				new FileSystemResource(mpnMpidMismatchSettings.getReportOutputFolder() + slash + filename));

		return new FlatFileItemWriterBuilder<MpnMpidProductItem>().resource(mpnMpidMismatchFileSystemResource.getFileSystemResource())
				.name("mpmMpidMismatchReportWriter")
				.shouldDeleteIfEmpty(true)
				.delimited().names(names)
				.headerCallback(writer -> writer.write(String.join(",", names))).build();
	}

	/*
	 * Decider that determines if there are any missing feiMPID records that need inserted.
	 */
	@Bean
	public MpnMpidMismatchMpidBackfillDecider mpnMpidMismatchMpidBackfillDecider() {
		return new MpnMpidMismatchMpidBackfillDecider();
	}

	@Bean
	@StepScope
	public MpnMpidMismatchItemProcessor mpnMpidMismatchItemProcessor() {
		return new MpnMpidMismatchItemProcessor(pdmMdmWebServicesFeignClient);
	}

	@Bean
	@JobScope
	public MpnMpidMismatchFileSystemResource mpnMpidMismatchFileSystemResource() {
		return new MpnMpidMismatchFileSystemResource();
	}

	@Bean
	public MpnMpidMismatchEmailReportTasklet mpnMpidMismatchSendReportTasklet() {
		return new MpnMpidMismatchEmailReportTasklet(mpnMpidMismatchSettings,buildWebServicesFeignClient);
	}

	@Bean
	public MpnMpidMismatchInsertMissingMpidTasklet insertMissingMpidTasklet() {
		return new MpnMpidMismatchInsertMissingMpidTasklet(mpnMpidMismatchService);
	}

}
