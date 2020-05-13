package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ResourceAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.exceprion.FeiPriceUpdateException;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemProcessor  implements ItemProcessor<FeiPriceUpdateItem, FeiPriceUpdateItem>  { 

	private final FeiPriceUpdateService feiPriceUpdateService;
	
	@Autowired 
	FeiPriceUpdateSettings feiPriceUpdateSettings;
	
	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;
	
	public FeiPriceUpdateItemProcessor(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}
	
	@Override
	public FeiPriceUpdateItem process(FeiPriceUpdateItem item) throws Exception {
		
		// Save off the input resource name.  Will need that later when we create the CostUploader job
		if (!executionContext.containsKey("inputFileName") && !StringUtils.isEmpty(item.getInputFileName())) {
			this.executionContext.put("inputFileName", item.getInputFileName());
		}
		
		if (item != null) {
			item.setTempTableName(feiPriceUpdateSettings.getTempTableName());
			FeiPriceUpdateItem productDetails = feiPriceUpdateService.getPriceUpdateProductDetails(item);
			
			if (productDetails != null) {
				item.setManufacturerId(productDetails.getManufacturerId());
				item.setUmrpId(productDetails.getUmrpId());
				item.setFeiOwnedProductId(productDetails.getFeiOwnedProductId());
				item.setBaseCategoryId(productDetails.getBaseCategoryId());
				// Here our item record is a customer priced item.  In the writer I will create a 2nd
				// pro pricing record for pricebookId 22 with the calculated pro price.
				item.setPricebookId(1);
			}
		}
		return item;
	}
}
