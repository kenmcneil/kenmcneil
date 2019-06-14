package com.ferguson.cs.vendor.quickship.service.category;

import org.springframework.stereotype.Repository;
import com.ferguson.cs.vendor.quickship.model.category.ShippingCategory;

@Repository
public class CategoryDaoImpl implements CategoryDao {

	private final CategoryMapper categoryMapper;

	public CategoryDaoImpl(CategoryMapper categoryMapper) {
		this.categoryMapper = categoryMapper;
	}


	@Override
	public ShippingCategory getStoreShippingCategory(Integer siteId, Integer storeId, Integer shippingCalculationNameId) {
		return categoryMapper
				.getGenericShippingCategory(siteId, storeId,shippingCalculationNameId);
	}

	@Override
	public ShippingCategory getUniqueIdShippingCategory(Integer genericCategoryId, Integer productUniqueId, Integer shippingCalculationNameId) {
		return categoryMapper.getUniqueIdShippingCategory(genericCategoryId,productUniqueId,shippingCalculationNameId);
	}
}
