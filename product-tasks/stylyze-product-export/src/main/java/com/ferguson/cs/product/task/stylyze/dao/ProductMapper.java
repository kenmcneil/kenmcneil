package com.ferguson.cs.product.task.stylyze.dao;

import java.util.List;

import com.ferguson.cs.product.task.stylyze.model.*;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;


@Mapper
public interface ProductMapper {
    List<Product> getProductData(@Param("familyId") int familyId);
    Product getProductByUniqueId(@Param("uniqueId") int uniqueId);
    List<ProductSpec> getProductSpecs(@Param("familyId") int familyId, @Param("application") String application, @Param("type") String type);
    List<ProductGalleryImage> getProductImages(@Param("manufacturer") String manufacturer, @Param("productId") String productId);
    Float getProductCost(@Param("uniqueId") int uniqueId);
    List<ProductCategory> getProductCategories(@Param("manufacturer") String manufacturer, @Param("productId") String productId);
    ProductCategory getCategory(@Param("categoryId") int categoryId);
    ProductRatings getProductRatings(@Param("familyId") Integer familyId);
    List<ProductVariation> getProductVariations(@Param("familyId") Integer familyId);
}

