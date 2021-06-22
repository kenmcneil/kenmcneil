package com.ferguson.cs.product.task.omnipriceharmonization.data.reporter;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.omnipriceharmonization.model.PriceHarmonizationData;

@Mapper
public interface OmniPriceHarmonizationMapper {
	void insertPriceHarmonizationData(@Param("priceHarmonizationData") PriceHarmonizationData priceHarmonizationData);
	void truncatePriceHarmonizationData();
}
