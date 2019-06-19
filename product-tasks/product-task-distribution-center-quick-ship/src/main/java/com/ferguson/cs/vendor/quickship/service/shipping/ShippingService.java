package com.ferguson.cs.vendor.quickship.service.shipping;

import com.ferguson.cs.vendor.quickship.model.shipping.ShippingCalculationView;

public interface ShippingService {
	/**
	 * Gets shipping calculation view for a given store and shipping calculation name
	 *
	 * @param siteId
	 * @param storeId
	 * @param shippingCalculationNameId
	 * @return default shipping calculation data for a given store and shipping calculation name
	 */
	ShippingCalculationView getStoreShippingCalculationView(Integer siteId, Integer storeId, Integer shippingCalculationNameId);

	/**
	 * Gets a shipping calculation view related to a specific product related to a generic category root id, if such a
	 * shipping calculation view exists.
	 *
	 * @param genericCategoryRootId
	 * @param productUniqueId
	 * @param shippingCalculationNameId
	 * @return shipping calculation override for a given product, null if that doesn't exist
	 */
	ShippingCalculationView getUniqueIdShippingCalculationView(Integer genericCategoryRootId, Integer productUniqueId, Integer shippingCalculationNameId);
}
