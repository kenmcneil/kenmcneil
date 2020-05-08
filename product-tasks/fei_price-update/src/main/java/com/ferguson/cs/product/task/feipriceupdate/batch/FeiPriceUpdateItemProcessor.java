package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemProcessor  implements ItemProcessor<FeiPriceUpdateItem, FeiPriceUpdateItem> { 

	@Override
	public FeiPriceUpdateItem process(FeiPriceUpdateItem item) throws Exception {

		if (item != null) {
			System.out.println("FEI Price Update Item : " + item.getUniqueId());
		}
		return item;
	}
}
