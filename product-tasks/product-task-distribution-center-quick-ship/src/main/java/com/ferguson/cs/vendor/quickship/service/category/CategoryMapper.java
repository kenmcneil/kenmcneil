package com.ferguson.cs.vendor.quickship.service.category;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ferguson.cs.vendor.quickship.model.category.ShippingCategory;

@Mapper
public interface CategoryMapper {

	ShippingCategory getGenericShippingCategory(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("shippingCalculationNameId") Integer shippingCalculationNameId);

	ShippingCategory getUniqueIdShippingCategory(@Param("genericCategoryId") Integer genericCategoryId, @Param("productUniqueId") Integer productUniqueId, @Param("shippingCalculationNameId") Integer shippingCalculationNameId);
}
