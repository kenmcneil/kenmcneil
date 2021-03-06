package com.ferguson.cs.product.task.feipriceupdate.data;

import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;

public interface FeiPriceUpdateDao {

	/*
	 * Create temporary price update processing table
	 * @Param tempTableName
	 */
	public void createTempTable(String tempTableName);

	/*
	 * Drop temporary work table
	 * @Param tempTableName
	 */
	public void dropTempTable(String tempTableName);

	/**
	 * Insert a price update record into our temp staging table
	 *
	 * @Param TempTableName
	 * @param FeiPriceUpdateItem
	 */
	public void insertTempPriceUpdateRecord(FeiPriceUpdateItem item);

	/**
	 * This method will return the product details required for PB1 processing
	 *
	 * @param FeiPriceUpdateItem
	 */
	public FeiPriceUpdateItem getPb1PriceUpdateProductDetails(FeiPriceUpdateItem item);

	/**
	 * This method will return the product details required for PB22 processing
	 *
	 * @param FeiPriceUpdateItem
	 */
	public FeiPriceUpdateItem getPb22PriceUpdateProductDetails(FeiPriceUpdateItem item);

	/**
	 * This method loads the tempData.dbo.pricebookCostUpdates table by doing a
	 * select from the temp table
	 *
	 * @param priceBookLoadCriteria
	 */
	public Integer loadPriceBookCostUpdatesFromTempTable(PriceBookLoadCriteria priceBookLoadCriteria);

	/**
	 * This method inserts a new cost update record into the costUpdateJob temporary
	 * table, which is used to process and track a cost update job. The following
	 * fields are required: processType, jobName, userId, processOn, and status.
	 *
	 * @param costUpdateJob
	 */
	public void insertCostUpdateJob(CostUpdateJob costUpdateJob);

	/**
	 * This method updates an existing cost update record in the costUpdateJob
	 * table, which is used to process and track a cost update job. The following
	 * fields are required: processType, jobName, userId, processOn, and status.
	 *
	 * @param costUpdateJob
	 */
	void updateCostUpdateJob(CostUpdateJob costUpdateJob);

	/**
	 * This method retrieves an existing CostUpdateJob record from the temporary
	 * costUpdateJob table using the required costUpdateJobId parameter.
	 *
	 * @param costUpdateJobId
	 * @return
	 */
	CostUpdateJob getCostUpdateJob(Integer costUpdateJobId);

	/**
	 * This method triggers the PriceBook Cost stored procedure
	 * (mmc.dbo.dsp_pricebookCostUpdater) which uses the temporary PriceBook cost
	 * table data and persists the new PriceBook cost values for products. Cost
	 * Update Job ID cannot be null and the cost update job must exist before the
	 * job can be executed.
	 *
	 * @param costUpdateJobId
	 */
	void executePriceBookCostUpdater(Integer costUpdateJobId);

	/**
	 * Validate that the mpid and uniqueId map to a record mmc.product.feimpid
	 * @Param mpid
	 * @Param uniqieId
	 */
	Boolean isValidMpidUniqueId(Integer mpid, Integer uniqueId);

	/**
	 * Retrieve the preferred vendor cost
	 * @Param uniqueId
	 * @Return Double
	 */
	Double getPreferredVendorCost(Integer uniqueId);

	/**
	 * Update the pricebook input record status code and status message for the given item.uniqueId.
	 * @param item
	 */
	void updateTempPriceUpdateRecordStatus(FeiPriceUpdateItem item);

}
