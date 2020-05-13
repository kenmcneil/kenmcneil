package com.ferguson.cs.product.task.feipriceupdate.data;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;
import com.ferguson.cs.product.task.feipriceupdate.model.ProductSyncJob;

@Mapper
public interface FeiPriceUpdateMapper {

	void createTempTable( String tempTableName);
	void dropTempTable(String tempTableName);	
	void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	FeiPriceUpdateItem getTempFeiPriceUpdateData(FeiPriceUpdateItem item);
	
	void insertProductSyncJob(ProductSyncJob productSyncJob);
	
	FeiPriceUpdateItem getPriceUpdateProductDetails(FeiPriceUpdateItem item);
	

	/**
	 * This method inserts a new record into the tempData.dbo.costUpdateJob table and the following fields cannot
	 * be null: processType, jobName, userId, createdOn, processOn, and status.
	 * @param costUpdateJob
	 */
	void insertCostUpdateJob(CostUpdateJob costUpdateJob);
	
	void insertPriceBookCostUpdates(PriceBookSync productSync);
	void loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria);
}
