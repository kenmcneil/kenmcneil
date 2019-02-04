package com.ferguson.cs.product.task.stylyze.dao;

import java.util.List;

import com.ferguson.cs.product.task.stylyze.model.*;

public interface ProductDao {

    List<Product> getProductData(int familyId);
    Product getProductByUniqueId(int uniqueId);
    List<ProductSpec> getProductSpecs(int familyId, String application, String type);
    List<ProductGalleryImage> getProductImages(String manufacturer, String productId);
    Float getProductCost(int uniqueId);
    List<ProductCategory> getProductCategories(String manufacturer, String productId);
    ProductCategory getCategory(int categoryId);
    ProductRatings getProductRatings(Integer familyId);
    List<ProductVariation> getProductVariations(Integer familyId);
}
