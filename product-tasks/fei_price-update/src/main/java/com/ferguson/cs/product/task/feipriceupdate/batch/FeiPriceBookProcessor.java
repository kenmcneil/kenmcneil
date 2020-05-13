package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.feipriceupdate.exceprion.FeiPriceUpdateException;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;

public class FeiPriceBookProcessor  implements ItemProcessor<FeiPriceUpdateItem, PriceBookSync> {

	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;

	@Override
	public PriceBookSync process(FeiPriceUpdateItem item) throws Exception {
		
		if (!this.executionContext.containsKey("JobId")) {
			throw new FeiPriceUpdateException(
					"FeiPriceUpdate Error: Job ID not defined in Job ExecutionContext");
		}
		
		PriceBookSync priceBookSync = new PriceBookSync();
		priceBookSync.setJobId(this.executionContext.getInt("JobId"));
		priceBookSync.setUniqueId(item.getUniqueId());
		priceBookSync.setCost(item.getPrice());
		priceBookSync.setPriceBookId(item.getPricebookId());
		priceBookSync.setIsDelete(false);
		
		return priceBookSync;
	}
}

