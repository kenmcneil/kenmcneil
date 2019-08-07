package com.ferguson.cs.product.task.wiser.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.wiser.model.WiserPerformanceData;

public class WiserPerformanceDataProcessor implements ItemProcessor<WiserPerformanceData,WiserPerformanceData> {
	@Override
	public WiserPerformanceData process(WiserPerformanceData item) throws Exception {
		if(!isValid(item)) {
			return null;
		}

		if(item.getMarketPlaceId() == 6) {
			item.setChannel(100);
		} else if(item.getMarketPlaceId() != 0) {
			item.setChannel(101);
		}
		return item;
	}

	private boolean isValid(WiserPerformanceData item) {
		return item != null &&
				item.getSku() != null &&
				item.getTransactionId() != null &&
				item.getUnits() != null &&
				item.getUnits() > 0;
	}
}
