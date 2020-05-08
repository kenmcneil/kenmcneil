package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;

public class FeiPriceUpdateJobListener implements JobExecutionListener {
	

	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;
	
	public FeiPriceUpdateJobListener(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		feiPriceUpdateService.dropTempTable(feiPriceUpdateSettings.getTempTableName());
	}

}
