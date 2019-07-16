package com.ferguson.cs.vendor.quickship.service.category;

import org.springframework.stereotype.Service;

import com.ferguson.cs.vendor.quickship.model.category.ShippingCalculationView;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryDao categoryDao;

	CategoryServiceImpl(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

	@Override
	public ShippingCalculationView getStoreShippingCalculationView(Integer siteId, Integer storeId, Integer shippingCalculationNameId) {
		return categoryDao.getStoreShippingCalculationView(siteId, storeId, shippingCalculationNameId);
	}

	@Override
	public ShippingCalculationView getUniqueIdShippingCalculationView(Integer genericCategoryRootId, Integer productUniqueId, Integer shippingCalculationNameId) {
		return categoryDao
				.getUniqueIdShippingCalculationView(genericCategoryRootId, productUniqueId, shippingCalculationNameId);
	}
}
