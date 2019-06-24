package com.ferguson.cs.vendor.quickship.service.category;

import org.springframework.stereotype.Repository;
import com.ferguson.cs.vendor.quickship.model.category.ShippingCalculationView;

@Repository
public class CategoryDaoImpl implements CategoryDao {

	private final CategoryMapper categoryMapper;

	public CategoryDaoImpl(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}


	@Override
	public ShippingCalculationView getStoreShippingCalculationView(Integer siteId, Integer storeId, Integer shippingCalculationNameId) {
		return categoryMapper
				.getStoreShippingCalculationView(siteId, storeId, shippingCalculationNameId);
	}

	@Override
	public ShippingCalculationView getUniqueIdShippingCalculationView(Integer genericCategoryRootId, Integer productUniqueId, Integer shippingCalculationNameId) {
		return categoryMapper
				.getUniqueIdShippingCalculationView(genericCategoryRootId, productUniqueId, shippingCalculationNameId);
	}
}
