package com.ferguson.cs.product.task.wiser.dao.core;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.wiser.model.ProductData;

@Mapper
public interface WiserMapper {
	List<ProductData> getProductData();
}
