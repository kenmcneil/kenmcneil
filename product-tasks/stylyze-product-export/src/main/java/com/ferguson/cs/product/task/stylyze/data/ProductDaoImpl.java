package com.ferguson.cs.product.task.stylyze.data;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.stylyze.model.Product;
import com.ferguson.cs.product.task.stylyze.model.ProductCategory;
import com.ferguson.cs.product.task.stylyze.model.ProductGalleryImage;
import com.ferguson.cs.product.task.stylyze.model.ProductRatings;
import com.ferguson.cs.product.task.stylyze.model.ProductSpec;
import com.ferguson.cs.product.task.stylyze.model.ProductVariation;
import com.ferguson.cs.utilities.ArgumentAssert;

@Repository
public class ProductDaoImpl implements ProductDao {

	private final ProductMapper productMapper;

	public ProductDaoImpl(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}

	@Override
	public List<Product> getProductData(int familyId) {
		return productMapper.getProductData(familyId);
	}

	@Override
	public List<ProductSpec> getProductSpecs(int familyId, String application, String type) {
		ArgumentAssert.notNullOrEmpty(application, "application");
		ArgumentAssert.notNullOrEmpty(type, "type");

		return productMapper.getProductSpecs(familyId, application, type);
	}

	@Override
	public List<ProductGalleryImage> getProductImages(String manufacturer, String productId) {
		ArgumentAssert.notNullOrEmpty(manufacturer, "manufacturer");
		ArgumentAssert.notNullOrEmpty(productId, "productId");

		return productMapper.getProductImages(manufacturer, productId);
	}

	@Override
	public Float getProductCost(int uniqueId) {
		return productMapper.getProductCost(uniqueId);
	}

	@Override
	public List<ProductCategory> getProductCategories(String manufacturer, String productId) {
		ArgumentAssert.notNullOrEmpty(manufacturer, "manufacturer");
		ArgumentAssert.notNullOrEmpty(productId, "productId");

		return productMapper.getProductCategories(manufacturer, productId);
	}

	@Override
	public ProductCategory getCategory(int categoryId) {
		return productMapper.getCategory(categoryId);
	}

	@Override
	public ProductRatings getProductRatings(int familyId) {

		return productMapper.getProductRatings(familyId);
	}

	@Override
	public List<ProductVariation> getProductVariations(Integer familyId) {
		ArgumentAssert.notNull(familyId, "familyId");

		return productMapper.getProductVariations(familyId);
	}

}
