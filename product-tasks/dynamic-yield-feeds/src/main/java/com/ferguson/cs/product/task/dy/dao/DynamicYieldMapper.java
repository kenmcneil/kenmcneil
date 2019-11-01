package com.ferguson.cs.product.task.dy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.dy.model.ProductData;

@Mapper
public interface DynamicYieldMapper {
	List<ProductData> getProductData();
}
