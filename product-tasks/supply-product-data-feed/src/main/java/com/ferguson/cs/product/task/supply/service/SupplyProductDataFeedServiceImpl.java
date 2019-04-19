package com.ferguson.cs.product.task.supply.service;

import java.util.Date;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ferguson.cs.task.batch.util.JobRepositoryHelper;

@Service
public class SupplyProductDataFeedServiceImpl implements SupplyProductDataFeedService{

	private JobRepositoryHelper jobRepositoryHelper;

	@Autowired
	public void setJobRepositoryHelper(JobRepositoryHelper jobRepositoryHelper) {
		this.jobRepositoryHelper = jobRepositoryHelper;
	}

	@Override
	public Date getLastRanDate(String jobName) {
		JobExecution jobExecution = jobRepositoryHelper.getLastJobExecution(jobName, ExitStatus.COMPLETED,25);
		if(jobExecution != null) {
			return  jobExecution.getEndTime();
		}
		return null;
	}
}
