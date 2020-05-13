package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateServiceImpl;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeiPriceUpdateItemWriter.class);
	private static final int LIGHTING_BASE_CATEGORY_ID = 4;
	
	@Autowired 
	FeiPriceUpdateSettings feiPriceUpdateSettings;
	
	private final FeiPriceUpdateService feiPriceUpdateService;
	
	public FeiPriceUpdateItemWriter(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {
System.out.println("Writing temp records");
		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>)items) {
			
			if (validPriceUpdateRecord(item)) {
				feiPriceUpdateService.insertTempPriceUpdateRecord(feiPriceUpdateSettings.getTempTableName(), item);	
				
				// Create a record for Pro pricing
				FeiPriceUpdateItem proItem = buildProPricingRecord(item);
				feiPriceUpdateService.insertTempPriceUpdateRecord(feiPriceUpdateSettings.getTempTableName(), proItem);	
			} 
		}		
	}
		
	/*
	 * Sanity check to validate the record.
	 */
	private boolean validPriceUpdateRecord(FeiPriceUpdateItem item) {
		
		if (item.getFeiOwnedProductId() == null) {
			LOGGER.debug("FeiPriceUpdateItemWriter - Skipping product unique ID : {}. Not FEI owned", item.getUniqueId());
			return false;
		} else if (item.getPrice() == null || item.getPrice() == 0) {
			LOGGER.info("FeiPriceUpdateItemWriter - Skipping product unique ID : {}. price supplied is null or zero.", item.getUniqueId());
			return false;
		}
		return true;
	}
	
	/*
	 * Create a 2nd pro pricing record with cost calculations
	 */
	private FeiPriceUpdateItem buildProPricingRecord(FeiPriceUpdateItem item) {
		FeiPriceUpdateItem proPriceUpdateItem = (FeiPriceUpdateItem) SerializationUtils.clone(item);
		proPriceUpdateItem.setPricebookId(22);
		proPriceUpdateItem.setPrice(calculateProPricing(item));
		return proPriceUpdateItem;
	}

	/*
	 * calculate Pro pricing based on the following criteria:
	 * Lighting Product Base Category: PB22 = PC24 x .9
	*  Everything else: PB22 = PC24 x .97
	*  UMRP Skus: PB22 = PC24  (No discount)
	 */
	private Double calculateProPricing(FeiPriceUpdateItem item) {
		// Default to customer price
		BigDecimal proPrice = new BigDecimal(Double.toString(item.getPrice()));

		// if UMRP then pro pricing is same as customer.
		if (item.getUmrpId() == null) {
			if (item.getBaseCategoryId() != null && item.getBaseCategoryId() == LIGHTING_BASE_CATEGORY_ID) {
				LOGGER.debug("calculateProPricing - Applying pro pricing multiplier: [.9] to product unique ID: {}", item.getUniqueId());
				proPrice = proPrice.multiply(new BigDecimal(".9"));
			} else {
				LOGGER.debug("calculateProPricing - Applying pro pricing multiplier: [.97] to product unique ID: {}", item.getUniqueId());
				proPrice = proPrice.multiply(new BigDecimal(".97"));
			}
		} else {
			LOGGER.debug("calculateProPricing - No pro pricing multiplier for product unique ID: {}", item.getUniqueId());
		}
		
		return proPrice.doubleValue();
	}
}
