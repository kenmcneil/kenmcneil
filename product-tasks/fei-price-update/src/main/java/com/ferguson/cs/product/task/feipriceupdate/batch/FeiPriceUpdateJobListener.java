package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.notification.NotificationService;
import com.ferguson.cs.product.task.feipriceupdate.notification.SlackMessageType;

public class FeiPriceUpdateJobListener implements JobExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateJobListener.class);

	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;
	private final NotificationService notificationService;

	public FeiPriceUpdateJobListener(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService,
			NotificationService notificationService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.notificationService = notificationService;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// Not Implemented
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

		feiPriceUpdateService.dropTempTable(feiPriceUpdateSettings.getTempTableName());

		List<Throwable> exceptions = jobExecution.getAllFailureExceptions();

		if (!exceptions.isEmpty()) {
			LOGGER.info("FEI Price update dataflow task job: {},  execution exceptions detected", jobExecution.getJobInstance().getJobName());
			for (Throwable th : exceptions) {
				LOGGER.error("exception has occurred in job.", th);
			}

			notificationService.message("FEI Price Update DataFlow task: "
					+ jobExecution.getJobInstance().getJobName()
					+ ", Exception encountered during execution: "
					+ exceptions.get(0).getMessage(), SlackMessageType.WARNING);
		}
	}
}
