package com.ferguson.cs.product.task.wiser.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.wiser.model.WiserPerformanceData;

public class WiserPerformanceDataProcessor implements ItemProcessor<WiserPerformanceData,WiserPerformanceData> {
	@Override
	public WiserPerformanceData process(WiserPerformanceData item) throws Exception {
		if(!isValid(item)) {
			return null;
		}

		if(item.getMarketplaceId() == 6) {
			item.setChannel(100);
		} else if(item.getMarketplaceId() != 0) {
			item.setChannel(101);
		}
		return item;
	}

	private boolean isValid(WiserPerformanceData item) {
		return item != null &&
				item.getSku() != null &&
				item.getChannel() != null &&
				item.getGrossUnits() != null &&
				item.getGrossUnits() > 0;
	}
}
