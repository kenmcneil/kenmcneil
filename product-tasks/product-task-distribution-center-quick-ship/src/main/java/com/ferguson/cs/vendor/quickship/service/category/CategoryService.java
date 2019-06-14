package com.ferguson.cs.vendor.quickship.service.category;

import com.ferguson.cs.vendor.quickship.model.category.ShippingCategory;

public interface CategoryService {
	ShippingCategory getStoreShippingCategory(Integer siteId, Integer storeId, Integer shippingCalculationNameId);
	ShippingCategory getUniqueIdShippingCategory(Integer genericCategoryId, Integer productUniqueId, Integer shippingCalculationNameId);
}
