package com.ferguson.cs.product.task.feipricefeed.service;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.task.feipricefeed.model.DeprioritizedBrandView;

public interface FeiPriceService {
	Date getLastRanDate(String jobName);

	Integer getNumberOfRunsToday(String jobName);

	void incrementNumberOfRunsToday(String jobName);

	List<DeprioritizedBrandView> getDeprioritizedBrandViews();

	void deleteStalePromoFeiPriceData();

	List<Integer> getStalePromoPriceProducts();
}
