package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemProcessor implements ItemProcessor<FeiPriceUpdateItem, FeiPriceUpdateItem> {

	private final FeiPriceUpdateService feiPriceUpdateService;

	@Autowired
	FeiPriceUpdateSettings feiPriceUpdateSettings;

	public FeiPriceUpdateItemProcessor(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}

	@Override
	public FeiPriceUpdateItem process(FeiPriceUpdateItem item) throws Exception {

		if (item != null) {
			// HACK - I could not get the passing of the temp table name into sql map via
			// @Param working if multiple params are required so I resorted to putting 
			// into the model object
			item.setTempTableName(feiPriceUpdateSettings.getTempTableName());
			FeiPriceUpdateItem productDetails = feiPriceUpdateService.getPriceUpdateProductDetails(item);

			if (productDetails != null) {
				item.setManufacturerId(productDetails.getManufacturerId());
				item.setUmrpId(productDetails.getUmrpId());
				item.setFeiOwnedProductId(productDetails.getFeiOwnedProductId());
				item.setBaseCategoryId(productDetails.getBaseCategoryId());
				// Here our item record is a customer priced item. In the writer I will create a
				// 2nd pro pricing record for pricebookId 22 with the calculated pro price.
				item.setPricebookId(1);
			}
		}
		return item;
	}
}
