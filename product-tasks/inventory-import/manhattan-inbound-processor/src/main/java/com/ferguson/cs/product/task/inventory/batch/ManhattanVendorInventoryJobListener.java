package com.ferguson.cs.product.task.inventory.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ManhattanVendorInventoryJobListener implements JobExecutionListener{

	@Override
	public void beforeJob(JobExecution jobExecution) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddyyyyhhmm");

		LocalDateTime localDateTime = LocalDateTime.now();

		jobExecution.getExecutionContext().putString("jobKey",localDateTime.format(dateTimeFormatter));
	}

	@Override
	public void afterJob(JobExecution jobExecution) {

	}

}
