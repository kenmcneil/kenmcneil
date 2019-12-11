package com.ferguson.cs.product.task.dy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ferguson.cs.product.task.dy.model.ProductData;

@Mapper
public interface DynamicYieldMapper {
	List<ProductData> getProductData(
			@Param("restrictionPolicies") List<Integer> restrictionPolicies,
			@Param("storeIds") List<Integer> storeIds);
}
