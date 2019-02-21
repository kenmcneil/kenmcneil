package com.ferguson.cs.product.task.stylyze.dao;

import java.util.List;

import com.ferguson.cs.product.task.stylyze.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDaoImpl implements ProductDao {
    private ProductMapper productMapper;

    @Autowired
    public void setProductMapper(ProductMapper productMapper){
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> getProductData(int familyId) {
        return productMapper.getProductData(familyId);
    }

    @Override
    public Product getProductByUniqueId(int uniqueId) {
        return productMapper.getProductByUniqueId(uniqueId);
    }

    @Override
    public List<ProductSpec> getProductSpecs(int familyId, String application, String type) { return productMapper.getProductSpecs(familyId, application, type); }

    @Override
    public List<ProductGalleryImage> getProductImages(String manufacturer, String productId) { return productMapper.getProductImages(manufacturer, productId); }

    @Override
    public Float getProductCost(int uniqueId) { return productMapper.getProductCost(uniqueId); }

    @Override
    public List<ProductCategory> getProductCategories(String manufacturer, String productId) { return productMapper.getProductCategories(manufacturer, productId); }

    @Override
    public ProductCategory getCategory(int categoryId) { return productMapper.getCategory(categoryId); }

    @Override
    public ProductRatings getProductRatings(Integer familyId) { return productMapper.getProductRatings(familyId); }

    @Override
    public List<ProductVariation> getProductVariations(Integer familyId) { return productMapper.getProductVariations(familyId); }
}
