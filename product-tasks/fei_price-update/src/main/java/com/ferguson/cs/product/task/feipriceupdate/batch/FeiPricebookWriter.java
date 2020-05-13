package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.exceprion.FeiPriceUpdateException;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;

public class FeiPricebookWriter implements ItemWriter<PriceBookSync> {

	@Autowired 
	FeiPriceUpdateSettings feiPriceUpdateSettings;
	
	private final FeiPriceUpdateService feiPriceUpdateService;
	
	public FeiPricebookWriter(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}
	
	@SuppressWarnings({"unchecked"})
	@Override
	public void write(List<? extends PriceBookSync> items) throws Exception {
		System.out.println("Writing pricebook records");
		for (PriceBookSync item : (List<PriceBookSync>)items) {
			feiPriceUpdateService.insertPriceBookCostUpdates(item);
		}			
	} 

}
