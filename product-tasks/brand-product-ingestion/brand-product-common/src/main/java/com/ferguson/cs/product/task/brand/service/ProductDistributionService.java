package com.ferguson.cs.product.task.brand.service;

import java.util.List;

import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.SystemSource;

public interface ProductDistributionService {

	/**
	 * Save a System Source
	 * 
	 * @param source
	 * @return unique id of source
	 */
	void saveSystemSource(SystemSource source);

	/**
	 * Save list of product
	 * 
	 * @param product
	 * @return unique id of product
	 */
	void saveProducts(List<BrandProduct> products);

	/**
	 * To delete the inactive products
	 * 
	 * @param systemSourceId
	 */
	void deleteStaleProducts(Integer systemSourceId);
}
