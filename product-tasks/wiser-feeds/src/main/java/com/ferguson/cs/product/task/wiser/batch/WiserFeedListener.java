package com.ferguson.cs.product.task.wiser.batch;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.ThreeSixtyPiSettings;
import com.ferguson.cs.product.task.wiser.WiserFeedSettings;
import com.ferguson.cs.product.task.wiser.model.WiserFeedType;
import com.ferguson.cs.utilities.DateUtils;

public class WiserFeedListener implements JobExecutionListener {


	private WiserFeedType wiserFeedType;
	private WiserFeedSettings wiserFeedSettings;
	private ThreeSixtyPiSettings threeSixtyPiSettings;

	public WiserFeedListener(WiserFeedType wiserFeedType) {
		this.wiserFeedType = wiserFeedType;
	}


	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
	}

	@Autowired
	public void setThreeSixtyPiSettings(ThreeSixtyPiSettings threeSixtyPiSettings) {
		this.threeSixtyPiSettings = threeSixtyPiSettings;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		Date today = new Date();

		String filePrefix;
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter(wiserFeedSettings.getDateTimeFormat());

		String dateString;
		String remoteFilePath;

		switch (wiserFeedType) {
			case PRODUCT_CATALOG_FEED:
				filePrefix = "Customer_catalog_";
				break;
			case PRICE_FEED:
				filePrefix = "Price_";
				break;
			case COMPETITOR_FEED:
				filePrefix = "Competitor_data_";
				Date yesterday = DateUtils.addDaysToDate(new Date(),-1);
				dateString = DateUtils.dateToString(yesterday,dateTimeFormatter);
				remoteFilePath = threeSixtyPiSettings.getFtpFolder() + "buildcom_product-level-uniqueid_all-products_" + dateString + "_*.csv";
				jobExecution.getExecutionContext().putString("remoteDownloadFilePath",remoteFilePath);
				break;
			case PERFORMANCE_FEED:
				filePrefix = "Performance_";
				break;
			case RECOMMENDATION_FEED:
				filePrefix = "Recommendation_data_";
				dateString = DateUtils.dateToString(new Date(),dateTimeFormatter);
				remoteFilePath = wiserFeedSettings.getFtpOutputFolder() + "wiser_output_" + dateString + ".csv";
				jobExecution.getExecutionContext().putString("remoteDownloadFilePath",remoteFilePath);
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
		if(wiserFeedType == WiserFeedType.RECOMMENDATION_FEED) {
			File file = new File(wiserFeedSettings.getTemporaryLocalFilePath() + jobExecution.getExecutionContext().getString("fileName"));
			FileUtils.deleteQuietly(file);
		}
	}
}
