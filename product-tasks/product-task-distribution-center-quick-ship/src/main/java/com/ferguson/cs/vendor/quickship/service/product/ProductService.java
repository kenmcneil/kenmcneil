package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;

public interface ProductService {

	/**
	 * Get paginated list of Quick Ship eligible products
	 * @param pageNumber
	 * @return
	 */
	List<Product> getQuickShipProductList(int pageNumber);

	/**
	 * Get list of product lead time override rules
	 * @param criteria
	 * @return
	 */
	List<ProductLeadTimeOverrideRule> getLeadTimeOverrideRuleList(
			ProductLeadTimeOverrideRuleSearchCriteria criteria);

	boolean productIsFreeShipping(Product product);
}
