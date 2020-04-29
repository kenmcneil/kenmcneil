package com.ferguson.cs.product.task.feipricefeed.data;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

@Mapper
public interface FeiPriceMapper {
	FeiPriceData getFullFeiPriceData();

	FeiPriceData getFeiPriceChangesSinceLastRun(@Param("lastRanDate") Date lastRanDate);
}
