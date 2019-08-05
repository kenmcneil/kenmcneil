package com.ferguson.cs.product.task.wiser.batch;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.WiserFeedSettings;
import com.ferguson.cs.product.task.wiser.model.WiserFeedType;
import com.ferguson.cs.utilities.DateUtils;

public class WiserFeedListener implements JobExecutionListener {


	private WiserFeedType wiserFeedType;
	private WiserFeedSettings wiserFeedSettings;

	public WiserFeedListener(WiserFeedType wiserFeedType) {
		this.wiserFeedType = wiserFeedType;
	}


	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		Date today = new Date();

		String filePrefix;

		switch (wiserFeedType) {
			case PRODUCT_CATALOG_FEED:
				filePrefix = "Customer_catalog_";
				break;
			case COMPETITOR_FEED:
				filePrefix = "Competitor_data_";
				DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter(wiserFeedSettings.getDateTimeFormat());
				Date yesterday = DateUtils.addDaysToDate(new Date(),-1);
				String dateString = DateUtils.dateToString(yesterday,dateTimeFormatter);
				String remoteFileName = "buildcom_product-level_all-products_" + dateString + "_*.csv";
				jobExecution.getExecutionContext().putString("remoteFileName",remoteFileName);
				break;
			default:
				filePrefix = "";
		}

		String fileName = filePrefix + DateUtils
				.dateToString(today, DateUtils.getDateTimeFormatter(wiserFeedSettings.getDateTimeFormat())) + ".csv";


		jobExecution.getExecutionContext().putString("fileName",fileName);
		jobExecution.getExecutionContext().putString("jobName",jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		//implementation not needed
	}
}
