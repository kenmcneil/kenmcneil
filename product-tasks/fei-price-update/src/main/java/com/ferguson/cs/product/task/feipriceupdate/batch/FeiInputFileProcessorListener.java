package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import com.ferguson.cs.product.task.feipriceupdate.notification.NotificationService;
import com.ferguson.cs.product.task.feipriceupdate.notification.SlackMessageType;

public class FeiInputFileProcessorListener implements StepExecutionListener {

	private final NotificationService notificationService;

	public FeiInputFileProcessorListener(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Not implemented
	}

	/*
	 * Need the read count for the Cost Uploader Job creation/execution step for
	 * logging
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		int readCount = stepExecution.getReadCount();

		// Nothing read - thats a problem
		if (readCount == 0) {
			notificationService.message("FEI Price Update DataFlow task: "
					+ stepExecution.getJobExecution().getJobInstance().getJobName()
					+ ", input file exists but zero records read.", SlackMessageType.WARNING);
			return ExitStatus.FAILED;
		} else {
			stepExecution.getJobExecution().getExecutionContext().put("READ_COUNT", readCount);
			return null;
		}
	}
}
