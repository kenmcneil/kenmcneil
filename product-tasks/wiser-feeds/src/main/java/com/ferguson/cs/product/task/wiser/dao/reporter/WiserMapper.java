package com.ferguson.cs.product.task.wiser.dao.reporter;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserPerformanceData;
import com.ferguson.cs.product.task.wiser.model.WiserPriceData;

@Mapper
public interface WiserMapper {
	List<ProductData> getProductData();
	List<WiserPriceData> getWiserPriceData(Date date);
	WiserPerformanceData getWiserPerformanceData(Date date);
	List<ProductRevenueCategory> getProductRevenueCategorization();
}