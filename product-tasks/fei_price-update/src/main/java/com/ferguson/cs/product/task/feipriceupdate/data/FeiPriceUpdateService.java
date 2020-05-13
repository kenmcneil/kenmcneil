package com.ferguson.cs.product.task.feipriceupdate.data;

import java.util.Date;

import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceType;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;
import com.ferguson.cs.product.task.feipriceupdate.model.ProductSyncJob;

public interface FeiPriceUpdateService {

	public void createTempTable(String tempTableName);
	public void dropTempTable(String tempTableName);
	void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item") FeiPriceUpdateItem item);	
	public CostUpdateJob createCostUploadJob(String fileName, CostPriceType type, Date processOn, Integer userId);
	public void insertPriceBookCostUpdates(PriceBookSync priceBookSync);	
	public FeiPriceUpdateItem getPriceUpdateProductDetails(FeiPriceUpdateItem item);
	public void loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria);

}
