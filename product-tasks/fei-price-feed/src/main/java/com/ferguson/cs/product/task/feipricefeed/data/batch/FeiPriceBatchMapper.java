package com.ferguson.cs.product.task.feipricefeed.data.batch;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

@Mapper
public interface FeiPriceBatchMapper {
	void updateFeiWhitelistPrice(FeiPriceData feiPriceData);
}
