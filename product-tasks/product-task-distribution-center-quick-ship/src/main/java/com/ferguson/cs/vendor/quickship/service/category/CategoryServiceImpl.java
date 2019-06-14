package com.ferguson.cs.vendor.quickship.service.category;

import org.springframework.stereotype.Service;
import com.ferguson.cs.vendor.quickship.model.category.ShippingCategory;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryDao categoryDao;


	CategoryServiceImpl(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public ShippingCategory getStoreShippingCategory(Integer siteId, Integer storeId, Integer shippingCalculationNameId) {
		return categoryDao.getStoreShippingCategory(siteId,storeId,shippingCalculationNameId);
	}

	@Override
	public ShippingCategory getUniqueIdShippingCategory(Integer genericCategoryId, Integer productUniqueId, Integer shippingCalculationNameId) {
		return categoryDao.getUniqueIdShippingCategory(genericCategoryId,productUniqueId,shippingCalculationNameId);
	}
}
