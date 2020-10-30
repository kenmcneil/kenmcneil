package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PricebookType;

public class FeiPriceUpdateItemProcessor implements ItemProcessor<FeiPriceUpdateItem, FeiPriceUpdateItem> {

	private final FeiPriceUpdateService feiPriceUpdateService;
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final PricebookType pricebookType;

	public FeiPriceUpdateItemProcessor(PricebookType pricebookType, FeiPriceUpdateService feiPriceUpdateService,FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.pricebookType = pricebookType;
	}

	@Override
	public FeiPriceUpdateItem process(FeiPriceUpdateItem item) throws Exception {

		if (item != null) {
			// HACK - I could not get the passing of the temp table name into sql map via
			// @Param working if multiple params are required so I resorted to putting
			// into the model object
			item.setTempTableName(feiPriceUpdateSettings.getTempTableName());
			item.setPricebookId(pricebookType.getIntValue());
			FeiPriceUpdateItem productDetails = feiPriceUpdateService.getPriceUpdateProductDetails(item);

			if (productDetails != null) {
				item.setManufacturerId(productDetails.getManufacturerId());
				item.setUmrpId(productDetails.getUmrpId());
				item.setFeiOwnedProductId(productDetails.getFeiOwnedProductId());
				item.setBaseCategoryId(productDetails.getBaseCategoryId());
				item.setFeiOwnedActive(productDetails.getFeiOwnedActive());
				item.setMapPrice(productDetails.getMapPrice());
				item.setProductStatus(productDetails.getProductStatus());
				item.setContainerType(productDetails.getContainerType());
				item.setFeiPricingType(productDetails.getFeiPricingType());
				item.setPricebookId(pricebookType.getIntValue());
				item.setNewPb1Price(productDetails.getNewPb1Price());
				item.setExistingPb1Price(productDetails.getExistingPb1Price());
			}
		}
		return item;
	}
}
