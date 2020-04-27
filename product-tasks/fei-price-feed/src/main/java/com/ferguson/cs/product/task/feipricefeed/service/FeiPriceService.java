package com.ferguson.cs.product.task.feipricefeed.service;

import java.util.Date;

public interface FeiPriceService {
	Date getLastRanDate(String jobName);

	Integer getNumberOfRunsToday(String jobName);

	void incrementNumberOfRunsToday(String jobName);
}
