package com.ferguson.cs.product.task.wiser.dao.integration;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

@Mapper
public interface WiserIntegrationMapper {
	List<WiserSale> getActiveOrModifiedWiserSales(@Param("date")Date date);
	List<ProductDataHash> getAllProductDataHashes();
	List<Integer> getProductDataHashUniqueIds();
	void truncateProductDataHashes();
}
