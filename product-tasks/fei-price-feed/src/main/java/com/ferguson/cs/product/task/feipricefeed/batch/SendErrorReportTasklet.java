package com.ferguson.cs.product.task.feipricefeed.batch;

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

import com.ferguson.cs.product.task.feipricefeed.FeiPriceSettings;
import com.ferguson.cs.product.task.feipricefeed.client.BuildWebServicesFeignClient;
import com.ferguson.cs.product.task.feipricefeed.model.EmailRequest;
import com.ferguson.cs.product.task.feipricefeed.model.EmailRequestBuilder;
import com.ferguson.cs.utilities.DateUtils;

public class SendErrorReportTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendErrorReportTasklet.class);
	private final FeiPriceSettings feiPriceSettings;
	private final BuildWebServicesFeignClient buildWebServicesFeignClient;

	public SendErrorReportTasklet(FeiPriceSettings feiPriceSettings,
								  BuildWebServicesFeignClient buildWebServicesFeignClient) {
		this.feiPriceSettings = feiPriceSettings;
		this.buildWebServicesFeignClient = buildWebServicesFeignClient;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		if (chunkContext.getStepContext().getJobExecutionContext().containsKey("errorReport")) {
			String errorReport = (String) chunkContext.getStepContext().getJobExecutionContext().get("errorReport");
			File csvFile = new File(errorReport);

			if (csvFile.exists() && csvFile.length() > 0) {
				LOGGER.debug("Sending error report : {}", errorReport);

				Date now = DateUtils.now();
				DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MM/dd/yyyy HH:mm:ss");
				String dateString = DateUtils.dateToString(now, dateTimeFormatter);

				if (feiPriceSettings.getErrorReportEmailList() == null || feiPriceSettings.getErrorReportEmailList().length == 0) {
					LOGGER.error("No recipient email address configured for error report");
				} else {
					String emailList = String.join(",", feiPriceSettings.getErrorReportEmailList());

					EmailRequest request = EmailRequestBuilder
							.sendTo(emailList)
							.from("noreply-scheduler@build.com")
							.subject("FEI Outbound Price Error Report (" + dateString +")")
							.templateName("EMPTY")
							.addTemplateData("body", "FEI Outbound pricing feed validation failures encountered. Please see the attached report for additional details.")
							.addRawAttachment(csvFile.getName(), Files.readAllBytes(csvFile.toPath()))
							.build();

					buildWebServicesFeignClient.queueEmail(request);
				}
			}
		}
		return RepeatStatus.FINISHED;
	}
}
