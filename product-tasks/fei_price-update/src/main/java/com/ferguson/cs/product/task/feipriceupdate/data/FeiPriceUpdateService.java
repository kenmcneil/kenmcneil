package com.ferguson.cs.product.task.feipriceupdate.data;

import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

public interface FeiPriceUpdateService {

	public void createTempTable(@Param("tempTableName") String tempTableName);
	public void dropTempTable(@Param("tempTableName") String tempTableName);
	void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item") FeiPriceUpdateItem item);
	void insertTempFeiOnlyPriceUpdate(@Param("tempTableName") String tempTableName, @Param("item") FeiPriceUpdateItem item);
}
