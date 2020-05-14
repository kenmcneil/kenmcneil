package com.ferguson.cs.product.task.feipriceupdate.data;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;

@Mapper
public interface FeiPriceUpdateMapper {

	/*
	 * Create temp DB staging table
	 * @Param tempTableName
	 */
	void createTempTable( String tempTableName);
	
	/* 
	 * Drop temp table
	 * @Param tempTableName
	 */
	void dropTempTable(String tempTableName);	
	
	/*
	 * Insert temp table record
	 * @Param FeiPriceUpdateItem
	 */
	void insertTempPriceUpdateRecord(FeiPriceUpdateItem item);
	
	/*
	 * Retrieve product details required for pricing calculations
	 * @Param FeiPriceUpdateItem
	 */
	FeiPriceUpdateItem getPriceUpdateProductDetails(FeiPriceUpdateItem item);	

	/**
	 * This method inserts a new record into the tempData.dbo.costUpdateJob table and the following fields cannot
	 * be null: processType, jobName, userId, createdOn, processOn, and status.
	 * @param costUpdateJob
	 */
	void insertCostUpdateJob(CostUpdateJob costUpdateJob);
	
	/**
	 * This method updates the status and processOn data for an existing tempData.dbo.costUpdateJob record.
	 * @param costUpdateJob
	 */
	void updateCostUpdateJob(CostUpdateJob costUpdateJob);
	
	/**
	 * This method retrieves an existing tempData.dbo.costUpdateJob record using the costUpdateJobId parameter.
	 * @param costUpdateJobId
	 * @return
	 */
	CostUpdateJob getCostUpdateJob(@Param("costUpdateJobId") Integer costUpdateJobId);
	
	/**
	 * This method loads the tempData.dbo.pricebookCostUpdates table by doing a
	 * select from the temp table
	 * 
	 * @param priceBookLoadCriteria
	 */
	Integer loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria);
	
	/**
	 * This method executes the stored procedure mmc.dbo.dsp_pricebookCostUpdater, which will process the data loaded
	 * into the tempData.dbo.pricebookCostUpdates.
	 * @param costUpdateJobId
	 */
	void executePricebookUpdater(@Param("costUpdateJobId") Integer costUpdateJobId);

}
