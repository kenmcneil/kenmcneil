package com.ferguson.cs.product.task.stylyze;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.stylyze.data.ProductDao;
import com.ferguson.cs.product.task.stylyze.model.Product;
import com.ferguson.cs.product.task.stylyze.model.ProductCategory;
import com.ferguson.cs.product.task.stylyze.model.ProductGalleryImage;
import com.ferguson.cs.product.task.stylyze.model.ProductRatings;
import com.ferguson.cs.product.task.stylyze.model.ProductSpec;
import com.ferguson.cs.product.task.stylyze.model.ProductVariation;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductDao productDao;

	public ProductServiceImpl(ProductDao productDao) {
		this.productDao = productDao;
	}

	public List<Product> getProductData(int familyId) {
		return this.productDao.getProductData(familyId);
	}

	public List<ProductSpec> getProductSpecs(int familyId, String application, String type) {
		ArgumentAssert.notNullOrEmpty(application, "application");
		ArgumentAssert.notNullOrEmpty(type, "type");

		return this.productDao.getProductSpecs(familyId, application, type);
	}

	public List<ProductGalleryImage> getProductImages(String manufacturer, String productId) {
		ArgumentAssert.notNullOrEmpty(manufacturer, "manufacturer");
		ArgumentAssert.notNullOrEmpty(productId, "productId");

		return this.productDao.getProductImages(manufacturer, productId);
	}

	public Float getProductCost(int uniqueId) {
		return this.productDao.getProductCost(uniqueId);
	}

	public List<ProductCategory> getProductCategories(String manufacturer, String productId) {
		ArgumentAssert.notNullOrEmpty(manufacturer, "manufacturer");
		ArgumentAssert.notNullOrEmpty(productId, "productId");

		return this.productDao.getProductCategories(manufacturer, productId);
	}

	public ProductCategory getCategory(int categoryId) {
		return this.productDao.getCategory(categoryId);
	}

	public ProductRatings getProductRatings(int familyId) {
		return this.productDao.getProductRatings(familyId);
	}

	public List<ProductVariation> getProductVariations(Integer familyId) {
		ArgumentAssert.notNull(familyId, "familyId");

		return this.productDao.getProductVariations(familyId);
	}

}
