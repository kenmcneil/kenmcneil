package com.ferguson.cs.product.task.wiser.dao.core;

import java.util.Map;

import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;

public interface WiserDao {
	/**
	 * Get revenue categorization for each product
	 *
	 * @return map of unique ids to revenue category
	 */
	Map<Integer, ProductRevenueCategory> getProductRevenueCategorization();
}
