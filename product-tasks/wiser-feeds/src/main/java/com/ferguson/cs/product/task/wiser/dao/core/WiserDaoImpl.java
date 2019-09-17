package com.ferguson.cs.product.task.wiser.dao.core;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;

@Repository
public class WiserDaoImpl implements WiserDao {
	private WiserMapper wiserMapper;

	@Autowired
	public void setWiserMapper(WiserMapper wiserMapper) {
		this.wiserMapper = wiserMapper;
	}

	@Override
	public Map<Integer, ProductRevenueCategory> getProductRevenueCategorization() {
		return wiserMapper.getProductRevenueCategorization().stream().collect(Collectors.toMap(ProductRevenueCategory::getProductUniqueId, Function.identity()));
	}
}
