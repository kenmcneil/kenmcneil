package com.ferguson.cs.product.task.feipriceupdate.batch;

import javax.batch.api.listener.StepListener;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;

public class FeiPriceUpdateReadListener implements StepListener {
	
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;

	public FeiPriceUpdateReadListener(FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}
	@Override
	public void beforeStep() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterStep() throws Exception {
		// TODO Auto-generated method stub
		
	}



}
