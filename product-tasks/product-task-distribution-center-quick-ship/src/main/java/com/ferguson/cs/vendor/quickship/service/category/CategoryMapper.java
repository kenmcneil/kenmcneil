package com.ferguson.cs.vendor.quickship.service.category;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.vendor.quickship.model.category.ShippingCalculationView;

@Mapper
public interface CategoryMapper {

	ShippingCalculationView getStoreShippingCalculationView(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("shippingCalculationNameId") Integer shippingCalculationNameId);

	ShippingCalculationView getUniqueIdShippingCalculationView(@Param("genericCategoryRootId") Integer genericCategoryRootId, @Param("productUniqueId") Integer productUniqueId, @Param("shippingCalculationNameId") Integer shippingCalculationNameId);
}
