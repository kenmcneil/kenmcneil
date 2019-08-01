package com.ferguson.cs.product.task.wiser.dao.integration;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

public interface WiserIntegrationDao {
	/**
	 * Gets sales that are active or that have been modified in a date range that ends with today.
	 *
	 * @param date  start of date range
	 * @return list of objects for products on sale
	 */
	List<WiserSale> getActiveOrModifiedWiserSales(Date date);

	/**
	 * Get all product data hash records
	 *
	 * @return entire list of product data hashes
	 */
	List<ProductDataHash> getAllProductDataHashes();

	/**
	 * Delete all product data hashes
	 */
	void truncateProductDataHashes();
}
