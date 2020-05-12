package com.ferguson.cs.product.task.feipriceupdate.data;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookSync;
import com.ferguson.cs.product.task.feipriceupdate.model.ProductSyncJob;

@Mapper
public interface FeiPriceUpdateMapper {

	void createTempTable(@Param("tempTableName") String tempTableName);
	void dropTempTable(@Param("tempTableName") String tempTableName);	
	void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	void insertTempFeiOnlyPriceUpdate(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	FeiPriceUpdateItem getTempFeiPriceUpdateData(@Param("item")FeiPriceUpdateItem item);
	
	void insertProductSyncJob(@Param("productSyncJob")ProductSyncJob productSyncJob);
	

	/**
	 * This method inserts a new record into the tempData.dbo.costUpdateJob table and the following fields cannot
	 * be null: processType, jobName, userId, createdOn, processOn, and status.
	 * @param costUpdateJob
	 */
	void insertCostUpdateJob(CostUpdateJob costUpdateJob);
	
	void insertPriceBookCostUpdates(PriceBookSync productSync);
}
