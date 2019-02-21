package com.ferguson.cs.product.task.stylyze.service;


import com.ferguson.cs.product.task.stylyze.dao.ProductDao;
import com.ferguson.cs.product.task.stylyze.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public ProductDao getProductDao() {
        return productDao;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Autowired
    private ProductDao productDao;

    public List<Product> getProductData(int familyId) {
        return this.productDao.getProductData(familyId);
    }

    public List<ProductSpec> getProductSpecs(int familyId, String application, String type) {
        return this.productDao.getProductSpecs(familyId, application, type);
    }

    public List<ProductGalleryImage> getProductImages(String manufacturer, String productId) {
        return this.productDao.getProductImages(manufacturer, productId);
    }

    public Float getProductCost(int uniqueId) {
        return this.productDao.getProductCost(uniqueId);
    }

    public List<ProductCategory> getProductCategories(String manufacturer, String productId) {
        return this.productDao.getProductCategories(manufacturer, productId);
    }

    public ProductCategory getCategory(int categoryId) {
        return this.productDao.getCategory(categoryId);
    }

    public ProductRatings getProductRatings(Integer familyId) {
        return this.productDao.getProductRatings(familyId);
    }

    public List<ProductVariation> getProductVariations(Integer familyId) { return this.productDao.getProductVariations(familyId); }
}

