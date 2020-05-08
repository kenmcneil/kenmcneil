package com.ferguson.cs.product.task.feipriceupdate.data;

import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public interface FeiPriceUpdateDao {
	
	/*
	 * Create temporary price update processing table
	 */
	public void createTempTable(@Param("tempTableName")String tempTableName);
	
	/*
	 * Drop temporary work table
	 */
	public void dropTempTable(@Param("tempTableName")String tempTableName);
		
	/*
	 * Insert or temp price update records.  The SQL invoked here will only insert if the uniqieId also exists in the 
	 * pricingFeiOwned table
	 */
	public void insertTempFeiOnlyPriceUpdate(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	
	// TODO - This will probably get deleted
	public void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	

}
