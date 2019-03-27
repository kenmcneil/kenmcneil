package com.ferguson.cs.product.task.stylyze.data;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.stylyze.model.Product;
import com.ferguson.cs.product.task.stylyze.model.ProductCategory;
import com.ferguson.cs.product.task.stylyze.model.ProductGalleryImage;
import com.ferguson.cs.product.task.stylyze.model.ProductRatings;
import com.ferguson.cs.product.task.stylyze.model.ProductSpec;
import com.ferguson.cs.product.task.stylyze.model.ProductVariation;

@Mapper
public interface ProductMapper {

	List<Product> getProductData(@Param("familyId") int familyId);

	List<ProductSpec> getProductSpecs(@Param("familyId") int familyId, @Param("application") String application, @Param("type") String type);

	List<ProductGalleryImage> getProductImages(@Param("manufacturer") String manufacturer, @Param("productId") String productId);

	Float getProductCost(@Param("uniqueId") int uniqueId);

	List<ProductCategory> getProductCategories(@Param("manufacturer") String manufacturer, @Param("productId") String productId);

	ProductCategory getCategory(@Param("categoryId") int categoryId);

	ProductRatings getProductRatings(@Param("familyId") Integer familyId);

	List<ProductVariation> getProductVariations(@Param("familyId") Integer familyId);

}
