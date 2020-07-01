package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.mpnmpidmismatch.MpnMpidMismatchSettings;
import com.ferguson.cs.product.task.mpnmpidmismatch.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.mpnmpidmismatch.util.EmailRequest;
import com.ferguson.cs.product.task.mpnmpidmismatch.util.EmailRequestBuilder;
import com.ferguson.cs.utilities.DateUtils;

public class MpnMpidMismatchEmailReportTasklet implements Tasklet{

	private static final Logger LOGGER = LoggerFactory.getLogger(MpnMpidMismatchEmailReportTasklet.class);

	private final MpnMpidMismatchSettings mpnMpidMismatchSettings;
	private final BuildWebServicesFeignClient buildWebservicesFeignClient;

	public MpnMpidMismatchEmailReportTasklet(MpnMpidMismatchSettings mpnMpidMismatchSettings,
			BuildWebServicesFeignClient buildWebservicesFeignClient) {
		this.mpnMpidMismatchSettings = mpnMpidMismatchSettings;
		this.buildWebservicesFeignClient = buildWebservicesFeignClient;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		File missingCsv = null;
		File mismatchCsv = null;

		// Our temp missing and mismatch csv files from previous steps
		if (chunkContext.getStepContext().getJobExecutionContext().containsKey("MISMATCH_REPORT")) {
			mismatchCsv = new File((String) chunkContext.getStepContext().getJobExecutionContext().get("MISMATCH_REPORT"));
		}

		if (chunkContext.getStepContext().getJobExecutionContext().containsKey("MISSING_REPORT")) {
			missingCsv = new File((String) chunkContext.getStepContext().getJobExecutionContext().get("MISSING_REPORT"));
		}

		// Build XLSX file name. This will get emailed out
		Date now = DateUtils.now();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("yyyyMMdd_HHmmss");
		String dateString = DateUtils.dateToString(now, dateTimeFormatter);
		String filename = String.format("%s_%s.xlsx", mpnMpidMismatchSettings.getEmailReportPrefix(), dateString );
		String slash = mpnMpidMismatchSettings.getReportOutputFolder().endsWith("/") ? "" : "/";
		String reportFile = mpnMpidMismatchSettings.getReportOutputFolder() + slash + filename;

		createXlsxFromCsv(missingCsv, mismatchCsv, reportFile);
		emailReport(reportFile);
		deleteFiles(missingCsv, mismatchCsv, reportFile);

		return RepeatStatus.FINISHED;
	}

	/*
	 * Create an XLSX file with separate tabs for the missing and mismatch data.
	 */
	private void createXlsxFromCsv(File missingCsv, File mismatchCsv, String reportFile) {
		try {
			XSSFWorkbook workBook = new XSSFWorkbook();
			XSSFSheet sheetMismatch = workBook.createSheet("MPN-MPID Mismatch");
			XSSFSheet sheetMissing = workBook.createSheet("feiMPID inserts");
			String currentLine=null;
			int RowNum=0;

			// Ignore the "number stored as text warnings".  Can't imaging have more that 9999 rows.
			sheetMismatch.addIgnoredErrors(new CellRangeAddress(0,9999,0,9999),IgnoredErrorType.NUMBER_STORED_AS_TEXT );
			sheetMissing.addIgnoredErrors(new CellRangeAddress(0,9999,0,9999),IgnoredErrorType.NUMBER_STORED_AS_TEXT );

			// highlight the columns that were inserted into feiMPID table in the
			// missing report.  That report shows additional data
			XSSFCellStyle highlighted = workBook.createCellStyle();
			highlighted.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
			highlighted.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// process mpn/mpid mismatch csv
			if (mismatchCsv.exists()) {
				RowNum=0;
				int numFields = 0;
				BufferedReader br = new BufferedReader(new FileReader(mismatchCsv));
				while ((currentLine = br.readLine()) != null) {

					// splits on comma outside the double quotes
					String str[] = currentLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
					numFields = str.length;
					XSSFRow currentRow=sheetMismatch.createRow(RowNum);

					for(int i=0;i<str.length;i++){
						currentRow.createCell(i).setCellValue(cleanString(str[i]));
					}

					RowNum++;
				}
				br.close();

				// Autosize column width to that of max column value length
				for (int i = 0 ; i < numFields ;i++) {
					sheetMismatch.autoSizeColumn(i);
				}
			} else {
				// No mismatches were found.  Indicate that in the report.
				XSSFRow currentRow=sheetMismatch.createRow(0);
				currentRow.createCell(0).setCellValue("No mismatch mpn-mpid data found");
			}

			// process missing mpid csv
			if (missingCsv.exists()) {
				RowNum=0;
				int numFields = 0;
				BufferedReader br = new BufferedReader(new FileReader(missingCsv));
				while ((currentLine = br.readLine()) != null) {

					String str[] = currentLine.split(",");
					numFields = str.length;
					XSSFRow currentRow=sheetMissing.createRow(RowNum);

					for(int i=0;i<str.length;i++){
						currentRow.createCell(i).setCellValue(cleanString(str[i]));

						// Highlight the values that were inserted into feiMPID
						if (i == 0 || i == (str.length - 1)) {
							currentRow.getCell(i).setCellStyle(highlighted);
						}
					}
					RowNum++;
				}
				br.close();

				// Autosize column width to that of max column value length
				for (int i = 0 ; i < numFields ;i++) {
					sheetMissing.autoSizeColumn(i);
				}
			} else {
				// No missing mpids were found.  Indicate that in the report.
				XSSFRow currentRow=sheetMissing.createRow(0);
				currentRow.createCell(0).setCellValue("No missing mpid data found");
			}

			FileOutputStream fileOutputStream =  new FileOutputStream(reportFile);
			workBook.write(fileOutputStream);
			fileOutputStream.close();
			workBook.close();
		} catch (Exception ex) {
			LOGGER.error("MpnMpidMismatchEmailReportTasklet caught exception: {}", ex.getMessage());
		}
	}

	private void emailReport(String reportFile) throws IOException {

		File xlxsFile = new File(reportFile);

		if (xlxsFile.exists() && xlxsFile.length() > 0) {
			LOGGER.debug("MpnMpidMismatchEmailReportTasklet - Sending mpn/mpid mismatch report : {}", reportFile);

			Date now = DateUtils.now();
			DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MM/dd/yyyy HH:mm:ss");
			String dateString = DateUtils.dateToString(now, dateTimeFormatter);

			if (mpnMpidMismatchSettings.getReportEmailList() == null || mpnMpidMismatchSettings.getReportEmailList().length == 0) {
				LOGGER.error("MpnMpidMismatchEmailReportTasklet - No recipient email address configured for error report");
			} else {
				String emailList = String.join(",", mpnMpidMismatchSettings.getReportEmailList());

				EmailRequest request = EmailRequestBuilder
						.sendTo(emailList)
						.from("noreply-scheduler@build.com")
						.subject("MPN/MPID mismatch Report (" + dateString +")")
						.templateName("EMPTY")
						.addTemplateData("body", "MPN/MPID mismatch report attached.")
						.addRawAttachment(xlxsFile.getName(), Files.readAllBytes(xlxsFile.toPath()))
						.build();

				buildWebservicesFeignClient.queueEmail(request);
			}
		}
	}

	/*
	 * Cleanup temp report files.
	 */
	private void deleteFiles(File missingCsv, File mismatchCsv, String reportFile) {
		if (missingCsv.exists()) {
			FileUtils.deleteQuietly(missingCsv);
		}

		if (mismatchCsv.exists()) {
			FileUtils.deleteQuietly(mismatchCsv);
		}

		File rptFile = new File(reportFile);

		if (rptFile.exists()) {
			FileUtils.deleteQuietly(new File(reportFile));
		}
	}

	// Our line aggregator calls StringEscapeUtils.escapeCsv() on the column text which will
	// wrap the String in double quotes and also escape inner quotes with the addition of
	// double quote when certain chars are found in the string.  This method will clean that
	// up so the column text is cleaner.
	private String cleanString(String value) {
		if (!StringUtils.isEmpty(value)) {

			// Remove beginning/ending double quotes
			if (value.startsWith("\"")) {
				value = value.substring(1, value.length());
			}
			if (value.endsWith("\"")) {
				value = value.substring(0, value.length() - 1);
			}

			// Now replace and repeating double quotes with a single quote
			return value.replaceAll("\"\"", "\"");

		} else {
			return value;
		}
	}

}
