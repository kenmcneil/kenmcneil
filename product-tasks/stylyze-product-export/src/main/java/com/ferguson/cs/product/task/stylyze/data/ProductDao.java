package com.ferguson.cs.product.task.stylyze.data;

import java.util.List;

import com.ferguson.cs.product.task.stylyze.model.Product;
import com.ferguson.cs.product.task.stylyze.model.ProductCategory;
import com.ferguson.cs.product.task.stylyze.model.ProductGalleryImage;
import com.ferguson.cs.product.task.stylyze.model.ProductRatings;
import com.ferguson.cs.product.task.stylyze.model.ProductSpec;
import com.ferguson.cs.product.task.stylyze.model.ProductVariation;

public interface ProductDao {

	List<Product> getProductData(int familyId);

	List<ProductSpec> getProductSpecs(int familyId, String application, String type);

	List<ProductGalleryImage> getProductImages(String manufacturer, String productId);

	Float getProductCost(int uniqueId);

	List<ProductCategory> getProductCategories(String manufacturer, String productId);

	ProductCategory getCategory(int categoryId);

	ProductRatings getProductRatings(int familyId);

	List<ProductVariation> getProductVariations(Integer familyId);

}
