package com.ferguson.cs.product.task.omnipriceharmonization.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.omnipriceharmonization.model.PriceHarmonizationData;

public class OmniPriceHarmonizationProcessor implements ItemProcessor<PriceHarmonizationData,PriceHarmonizationData> {
	@Override
	public PriceHarmonizationData process(PriceHarmonizationData item) throws Exception {
		if(item.getUniqueId() == null) {
			return null;
		}

		if(item.getMpid() != null && item.getMpid().equalsIgnoreCase("null")) {
			item.setMpid(null);
		}
		return item;
	}
}
