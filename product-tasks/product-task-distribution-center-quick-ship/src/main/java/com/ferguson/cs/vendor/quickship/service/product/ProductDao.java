package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;

public interface ProductDao {

	/**
	 * Get list of Quick Ship eligible products
	 * @param criteria
	 * @return
	 */
	List<Product> getQuickShipEligibleProduct(QuickshipEligibleProductSearchCriteria criteria);

	/**
	 * Get list of product lead time override rules
	 * @param criteria
	 * @return
	 */
	List<ProductLeadTimeOverrideRule> getProductLeadTimeOverrideRule(ProductLeadTimeOverrideRuleSearchCriteria criteria);

}
