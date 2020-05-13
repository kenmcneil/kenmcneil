package com.ferguson.cs.product.task.feipriceupdate.data;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceType;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;
import com.ferguson.cs.product.task.feipriceupdate.model.ProductSyncJob;

public interface FeiPriceUpdateDao {
	
	/*
	 * Create temporary price update processing table
	 */
	public void createTempTable(String tempTableName);
	
	/*
	 * Drop temporary work table
	 */
	public void dropTempTable(String tempTableName);
		
	public void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	
	public void insertCostUpdateJob(CostUpdateJob costUpdateJob);
	
	public void insertPriceBookCostUpdates(PriceBookSync priceBookSync);
	
	public FeiPriceUpdateItem getPriceUpdateProductDetails(FeiPriceUpdateItem item);
	
	public void loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria);

}
