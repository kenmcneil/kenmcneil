package com.ferguson.cs.product.task.feipriceupdate.batch;

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

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.feipriceupdate.model.EmailRequest;
import com.ferguson.cs.product.task.feipriceupdate.model.EmailRequestBuilder;
import com.ferguson.cs.utilities.DateUtils;

public class FeiSendErrorReportTasklet implements Tasklet {



	private static final Logger LOGGER = LoggerFactory.getLogger(FeiSendErrorReportTasklet.class);
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final BuildWebServicesFeignClient buildWebServicesFeignClient;

	public FeiSendErrorReportTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			BuildWebServicesFeignClient buildWebServicesFeignClient) {
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.buildWebServicesFeignClient = buildWebServicesFeignClient;
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

				if (feiPriceUpdateSettings.getErrorReportEmailList() == null || feiPriceUpdateSettings.getErrorReportEmailList().length == 0) {
					LOGGER.error("FeiSendErrorReportTasklet - No recipient email address configured for error report");
				} else {
					String emailList = String.join(",", feiPriceUpdateSettings.getErrorReportEmailList());

					EmailRequest request = EmailRequestBuilder
							.sendTo(emailList)
							.from("noreply-scheduler@build.com")
							.subject("FEI Price Update Error Report (" + dateString +")")
							.templateName("EMPTY")
							.addTemplateData("body", "FEI Pricing update errors encountered. Please see the attached error report for additional details.")
							.addRawAttachment(csvFile.getName(), Files.readAllBytes(csvFile.toPath()))
							.build();

					buildWebServicesFeignClient.queueEmail(request);
				}
			}
		}
		return RepeatStatus.FINISHED;
	}
}
