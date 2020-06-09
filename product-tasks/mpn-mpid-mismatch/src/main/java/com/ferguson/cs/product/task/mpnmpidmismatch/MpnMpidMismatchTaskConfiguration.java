package com.ferguson.cs.product.task.mpnmpidmismatch;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchEmailReportTasklet;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchFileSystemResource;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchInsertMissingMpidTasklet;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchItemProcessor;
import com.ferguson.cs.product.task.mpnmpidmismatch.batch.MpnMpidMismatchLineAggregator;
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
			Step createMpnMpidMissingReport,
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
	 * Create report with all the mismatch MPN/MPID's.  Will need to reach out to MDM for additional data
	 */
	@Bean
	public Step createMpnMpidMismatchReport(
			MyBatisCursorItemReader<MpnMpidProductItem> mpmMpidMismatchItemReader,
			MpnMpidMismatchItemProcessor mpnMpidMismatchItemProcessor,
			FlatFileItemWriter<MpnMpidProductItem> mpmMpidMismatchReportWriter) {
		return taskBatchJobFactory.getStepBuilder("writeMpnMpidMismatchData").<MpnMpidProductItem, MpnMpidProductItem>chunk(1000)
				.reader(mpmMpidMismatchItemReader).processor(mpnMpidMismatchItemProcessor).writer(mpmMpidMismatchReportWriter).build();
	}

	/*
	 * Step 4
	 * Email the mismatch report if it was created
	 */
	@Bean
	public Step emailReportTasklet() {
		return taskBatchJobFactory.getStepBuilder("emailReportTasklet")
				.tasklet(mpnMpidMismatchSendReportTasklet()).build();
	}

	/*
	 * Reader for Step 1
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

	/*
	 * Writer for Step 1
	 */
	@Bean
	@StepScope
	public FlatFileItemWriter<MpnMpidProductItem> mpmMpidMissingReportWriter(
			@Value("#{stepExecution.jobExecution}") JobExecution jobExecution,
			MpnMpidMismatchFileSystemResource mpnMpidMismatchFileSystemResource) {
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyyMMdd_HHmmss");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("%s_%s.csv", mpnMpidMismatchSettings.getMissingCsvPrefix(), dateString );
		String[] header = new String[]{"uniqueId", "productId", "manufacturer", "finish", "upc", "sku","mpn"};

		String slash = mpnMpidMismatchSettings.getReportOutputFolder().endsWith("/") ? "" : "/";
		String filepath = mpnMpidMismatchSettings.getReportOutputFolder() + slash + filename;

		mpnMpidMismatchFileSystemResource.setFileSystemResource(
				new FileSystemResource(filepath));

		jobExecution.getExecutionContext().put("MISSING_REPORT",filepath);

		BeanWrapperFieldExtractor<MpnMpidProductItem> extractor = new BeanWrapperFieldExtractor<MpnMpidProductItem>();
		extractor.setNames(camelCase(header));

		MpnMpidMismatchLineAggregator<MpnMpidProductItem> lineAggregator = new MpnMpidMismatchLineAggregator<>();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(extractor);

		return new FlatFileItemWriterBuilder<MpnMpidProductItem>().resource(mpnMpidMismatchFileSystemResource.getFileSystemResource())
				.name("mpmMpidMismatchReportWriter")
				.shouldDeleteIfEmpty(true)
				.lineAggregator(lineAggregator)
				.headerCallback(writer -> writer.write(String.join(",", header))).build();
	}

	/*
	 * Reader for Step 3
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

	/*
	 * Writer for Step 3
	 * Mismatch mpn/mpid report writer
	 */
	@Bean
	@StepScope
	public FlatFileItemWriter<MpnMpidProductItem> mpmMpidMismatchReportWriter(
			@Value("#{stepExecution.jobExecution}") JobExecution jobExecution,
			MpnMpidMismatchFileSystemResource mpnMpidMismatchFileSystemResource) {

		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyyMMdd_HHmmss");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("%s_%s.csv", mpnMpidMismatchSettings.getMismatchCsvPrefix(), dateString );
		String[] header = new String[]{
				"uniqueId",
				"product_id",
				"manufacturer",
				"finish",
				"mpn",
				"mdm_mpn_match",
				"mpid",
				"mdm_mpid_match",
				"sku",
				"mdm_mpn_sku",
				"mdm_mpid_sku",
				"upc",
				"mdm_mpn_upc",
				"mdm_mpid_upc",
				"mdm_mpn_Primary_vendor_id",
				"mdm_mpid_Primary_vendor_id",
				"mdm_mpn_description",
				"mdm_mpid_description",
				"mdm_mpn_alternate_code",
				"mdm_mpid_alternate_code"
		};

		String slash = mpnMpidMismatchSettings.getReportOutputFolder().endsWith("/") ? "" : "/";
		String filepath = mpnMpidMismatchSettings.getReportOutputFolder() + slash + filename;
		jobExecution.getExecutionContext().put("MISMATCH_REPORT",filepath);

		mpnMpidMismatchFileSystemResource.setFileSystemResource(
				new FileSystemResource(filepath));

		BeanWrapperFieldExtractor<MpnMpidProductItem> extractor = new BeanWrapperFieldExtractor<MpnMpidProductItem>();
		extractor.setNames(camelCase(header));

		MpnMpidMismatchLineAggregator<MpnMpidProductItem> lineAggregator = new MpnMpidMismatchLineAggregator<>();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(extractor);

		return new FlatFileItemWriterBuilder<MpnMpidProductItem>().resource(mpnMpidMismatchFileSystemResource.getFileSystemResource())
				.name("mpmMpidMismatchReportWriter")
				.shouldDeleteIfEmpty(true)
				.lineAggregator(lineAggregator)
				.headerCallback(writer -> writer.write(String.join(",", header))).build();
	}

	/*
	 * Decider that determines if there are any missing feiMPID records that need inserted.
	 */
	@Bean
	public MpnMpidMismatchMpidBackfillDecider mpnMpidMismatchMpidBackfillDecider() {
		return new MpnMpidMismatchMpidBackfillDecider();
	}

	/*
	 * Processor for Step 3
	 * Processor will do additional MDM lookups to gather additional details for the mismatch report
	 */
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

	/**
	 * Convert snake case to camel case
	 *
	 * @param columnNames The names of the columns to convert
	 * @return Camel cased string
	 */
	private String[] camelCase(String[] columnNames) {
		String[] updatedColumnNames = new String[columnNames.length];
		int index = 0;
		Pattern underscorePattern = Pattern.compile("_(.)");

		for (String columnName : columnNames) {
			Matcher underscoreMatcher = underscorePattern.matcher(columnName);
			StringBuffer sb = new StringBuffer();

			while (underscoreMatcher.find()) {
				underscoreMatcher.appendReplacement(sb, underscoreMatcher.group().toUpperCase());
			}

			underscoreMatcher.appendTail(sb);
			updatedColumnNames[index] = sb.toString().replaceAll("_","");
			index++;
		}
		return updatedColumnNames;
	}

}
