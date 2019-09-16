package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;

public interface ProductDao {

	/**
	 * Get list of Quick Ship eligible products
	 * @param criteria
	 * @return
	 */
	List<Product> getQuickShipEligibleProduct(QuickshipEligibleProductSearchCriteria criteria);

	/**
	 * This method will update an existing modified row for an existing product.
	 *
	 * @param product
	 */
	void updateProductModified(Product product);

}
