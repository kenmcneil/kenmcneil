package com.ferguson.cs.product.task.wiser.dao.reporter;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.wiser.model.ProductData;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserPerformanceData;
import com.ferguson.cs.product.task.wiser.model.WiserPriceData;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

@Mapper
public interface WiserReporterMapper {
	List<ProductData> getProductData();
	List<WiserPriceData> getWiserPriceData(Date date);
	WiserPerformanceData getWiserPerformanceData(Date date);
	List<ProductRevenueCategory> getProductRevenueCategorization();
	List<WiserSale> getParticipationProductSales(@Param("date") Date date);
}
