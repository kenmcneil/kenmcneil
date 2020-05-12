package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public class FeiPriceUpdateItemWriter implements ItemWriter<FeiPriceUpdateItem>{
	
	@Autowired 
	FeiPriceUpdateSettings feiPriceUpdateSettings;
	
	private final FeiPriceUpdateService feiPriceUpdateService;
	private int count;
	
	public FeiPriceUpdateItemWriter(FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.count = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends FeiPriceUpdateItem> items) throws Exception {

		for (FeiPriceUpdateItem item : (List<FeiPriceUpdateItem>)items) {
			//feiPriceUpdateService.insertTempPriceUpdateRecord(feiPriceUpdateSettings.getTempTableName(), item);
			try {
				feiPriceUpdateService.insertTempFeiOnlyPriceUpdate(feiPriceUpdateSettings.getTempTableName(), item);	
			} catch (Exception ex) {
				System.out.println (ex.getMessage());
			}
		}		
		this.count += items.size();
		System.out.println("Writing to temp table.. Record total: " + this.count);
	}

}
