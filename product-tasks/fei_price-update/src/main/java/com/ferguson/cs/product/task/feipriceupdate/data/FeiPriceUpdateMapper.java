package com.ferguson.cs.product.task.feipriceupdate.data;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.feipriceupdate.model.FeiPriceUpdateItem;

@Mapper
public interface FeiPriceUpdateMapper {

	void createTempTable(@Param("tempTableName") String tempTableName);
	void dropTempTable(@Param("tempTableName") String tempTableName);	
	void insertTempPriceUpdateRecord(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
	void insertTempFeiOnlyPriceUpdate(@Param("tempTableName") String tempTableName, @Param("item")FeiPriceUpdateItem item);
}
