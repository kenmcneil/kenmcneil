package com.ferguson.cs.product.task.feipricefeed.service;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.feipricefeed.data.core.FeiPriceCoreDao;
import com.ferguson.cs.product.task.feipricefeed.data.reporter.FeiPriceDao;
import com.ferguson.cs.product.task.feipricefeed.model.DeprioritizedBrandView;
import com.ferguson.cs.task.batch.util.JobRepositoryHelper;
import com.ferguson.cs.task.data.TaskControlData;
import com.ferguson.cs.task.data.TaskControlDataDao;
import com.ferguson.cs.task.data.TaskControlDataItem;
import com.ferguson.cs.utilities.DateUtils;

@Service
public class FeiPriceServiceImpl implements FeiPriceService {

	private static final Logger LOG = LoggerFactory.getLogger(FeiPriceServiceImpl.class);
	private final JobRepositoryHelper jobRepositoryHelper;
	private final TaskControlDataDao taskControlDataDao;
	private final FeiPriceDao feiPriceDao;
	private final FeiPriceCoreDao feiPriceCoreDao;

	public FeiPriceServiceImpl(JobRepositoryHelper jobRepositoryHelper, TaskControlDataDao taskControlDataDao, FeiPriceDao feiPriceDao, FeiPriceCoreDao feiPriceCoreDao) {
		this.jobRepositoryHelper = jobRepositoryHelper;
		this.taskControlDataDao = taskControlDataDao;
		this.feiPriceDao = feiPriceDao;
		this.feiPriceCoreDao = feiPriceCoreDao;
	}

	@Override
	public Date getLastRanDate(String jobName) {
		try {
			JobExecution jobExecution = jobRepositoryHelper.getLastJobExecution(jobName, ExitStatus.COMPLETED);
			if (jobExecution != null) {
				return jobExecution.getEndTime();
			}
		} catch (Exception e) {
			LOG.error("Failed to retrieve previous job execution data", e);
		}
		return  null;
	}

	@Override
	public Integer getNumberOfRunsToday(String jobName) {

		TaskControlDataItem taskControlDataItem = getTaskControlDataItemForToday(jobName);
		if(taskControlDataItem != null) {
			return Integer.parseInt(taskControlDataItem.getDataValue());
		}
		return 0;
	}

	@Override
	public void incrementNumberOfRunsToday(String jobName) {
		TaskControlData taskControlData = new TaskControlData();
		taskControlData.setCreatedDateTime(new Date());
		taskControlData.setTask(jobName);

		TaskControlDataItem taskControlDataItem = getTaskControlDataItemForToday(jobName);
		if(taskControlDataItem == null) {
			Date today = DateUtils.today();
			DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MMddyyyy");
			String dateString = DateUtils.dateToString(today,dateTimeFormatter);
			taskControlDataItem = new TaskControlDataItem();
			taskControlDataItem.setDataKey(dateString);
			taskControlDataItem.setDataValue("0");
		}
		int incremented = Integer.parseInt(taskControlDataItem.getDataValue()) + 1;
		taskControlDataItem.setDataValue(Integer.toString(incremented));
		taskControlData.addDataItem(taskControlDataItem);

		taskControlDataDao.insertControlData(taskControlData);
	}

	@Override
	public List<DeprioritizedBrandView> getDeprioritizedBrandViews() {
		return feiPriceDao.getDeprioritizedBrands();
	}


	@Override
	public void deleteStalePromoFeiPriceData() {
		feiPriceCoreDao.deleteStalePromoFeiPriceData();
	}

	@Override
	public List<Integer> getStalePromoPriceProducts() {
		return feiPriceDao.getStalePromoPriceProducts();
	}

	private TaskControlDataItem getTaskControlDataItemForToday(String jobName) {
		Date today = DateUtils.today();
		DateTimeFormatter dateTimeFormatter = DateUtils.getDateTimeFormatter("MMddyyyy");
		String dateString = DateUtils.dateToString(today,dateTimeFormatter);
		TaskControlData taskControlData =  taskControlDataDao.getCurrentControlData(jobName);
		if(taskControlData != null) {
			List<TaskControlDataItem> taskControlDataItems = taskControlDataDao.getCurrentControlData(jobName)
					.getDataItems();

			for (TaskControlDataItem item : taskControlDataItems) {
				if (item.getDataKey().equals(dateString)) {
					return item;
				}
			}
		}
		return null;
	}
}
