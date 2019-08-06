package com.ferguson.cs.product.task.wiser.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.wiser.model.WiserPriceData;

public class WiserPriceDataProcessor implements ItemProcessor<WiserPriceData,List<WiserPriceData>> {
	@Override
	public List<WiserPriceData> process(WiserPriceData item) throws Exception {
		List<WiserPriceData> wiserPriceData = new ArrayList<>();

		if(isValid(item)) {
			wiserPriceData.add(item);
			if(item.getChannel() == 1) {
				WiserPriceData channel100Copy = new WiserPriceData(item);
				channel100Copy.setChannel(100);
				WiserPriceData channel101Copy = new WiserPriceData(item);
				channel101Copy.setChannel(101);
				wiserPriceData.add(channel100Copy);
				wiserPriceData.add(channel101Copy);
			}
		}

		return wiserPriceData;
	}

	private boolean isValid(WiserPriceData priceData) {
		return priceData != null &&
				priceData.getSku() != null;
	}
}
