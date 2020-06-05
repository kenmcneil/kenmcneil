package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

		if (chunkContext.getStepContext().getJobExecutionContext().containsKey("ERROR_REPORT")) {
			String errorReport = (String) chunkContext.getStepContext().getJobExecutionContext().get("ERROR_REPORT");
			File csvFile = new File(errorReport);

			if (csvFile.exists() && csvFile.length() > 0) {
				LOGGER.debug("FeiSendErrorReportTasklet - Sending error report : {}", errorReport);

				Date now = DateUtils.now();
				DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MM/dd/yyyy HH:mm:ss");
				String dateString = DateUtils.dateToString(now, dateTimeFormatter);

				if (mpnMpidMismatchSettings.getReportEmailList() == null || mpnMpidMismatchSettings.getReportEmailList().length == 0) {
					LOGGER.error("FeiSendErrorReportTasklet - No recipient email address configured for error report");
				} else {
					String emailList = String.join(",", mpnMpidMismatchSettings.getReportEmailList());

					EmailRequest request = EmailRequestBuilder
							.sendTo(emailList)
							.from("noreply-scheduler@build.com")
							.subject("FEI Price Update Error Report (" + dateString +")")
							.templateName("EMPTY")
							.addTemplateData("body", "FEI Pricing update errors encountered. Please see the attached error report for additional details.")
							.addRawAttachment(csvFile.getName(), Files.readAllBytes(csvFile.toPath()))
							.build();

					buildWebservicesFeignClient.queueEmail(request);
				}
			}
		}
		return RepeatStatus.FINISHED;
	}
}
