package com.ferguson.cs.product.task.feipricefeed.data.core;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeiPriceCoreMapper {

	void deleteStalePromoFeiPriceData();
}
