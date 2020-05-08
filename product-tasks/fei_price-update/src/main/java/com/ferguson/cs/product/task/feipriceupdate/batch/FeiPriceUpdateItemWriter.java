package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem>{
	
	@Autowired 
	FeiPriceUpdateSettings feiPriceUpdateSettings;
	
	private final FeiPriceUpdateService feiPriceUpdateService;
	
	public FeiPriceUpdateItemWriter(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {

		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>)items) {
			System.out.println("Insert Item");
			//feiPriceUpdateService.insertTempPriceUpdateRecord(feiPriceUpdateSettings.getTempTableName(), item);
			feiPriceUpdateService.insertTempFeiOnlyPriceUpdate(feiPriceUpdateSettings.getTempTableName(), item);	
		}		
	}

}
