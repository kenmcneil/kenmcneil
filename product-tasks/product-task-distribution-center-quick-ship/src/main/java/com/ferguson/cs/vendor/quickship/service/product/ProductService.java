package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.category.ShippingCalculationView;

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


	/**
	 * Determines if a product has free shipping, based on a store shipping calculation view and product level overrides
	 *
	 * @param product
	 * @param storeShippingCalculationView
	 * @return true if free shipping, false otherwise
	 */
	boolean isFreeShipping(Product product, ShippingCalculationView storeShippingCalculationView);

}
