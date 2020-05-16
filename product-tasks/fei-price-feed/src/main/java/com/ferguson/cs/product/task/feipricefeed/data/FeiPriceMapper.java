package com.ferguson.cs.product.task.feipricefeed.data;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;

@Mapper
public interface FeiPriceMapper {
	List<FeiPriceData> getFullFeiPriceData();

	List<FeiPriceData> getFeiPriceChangesSinceLastRun(@Param("lastRanDate") Date lastRanDate);

	List<FeiPriceData> getFeiImapPriceData();
}
