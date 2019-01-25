package com.ferguson.cs.product.task.brand.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ferguson.cs.product.task.brand.model.JsonReference;
import com.ferguson.cs.product.task.brand.model.ProductJson;
import com.ferguson.cs.product.task.brand.model.BrandProduct;
import com.ferguson.cs.product.task.brand.model.SystemSource;


@Repository
public class ProductDistributionDaoImpl  implements ProductDistributionDao{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductDistributionDaoImpl.class);
	@Autowired
	ProductDistributionMapper mapper;
	
	
	
	@Override
	public void upsertSystemSource(SystemSource systemSource) {
		Assert.hasText(systemSource.getSourceName(), "SystemSourceName is required field.");
		Assert.notNull(systemSource.getActiveProductsFetched(), "ActiveProductsFetched cannot be null.");
		Assert.notNull(systemSource.getObsoleteProductsFetched(), "ObsoleteProductsFetched cannot be null.");
		mapper.upsertSystemSource(systemSource);
		int id = mapper.getSystemSourceId(systemSource.getSourceName());
		systemSource.setId(id);
		
	}
	
	@Override
	@Transactional("productDistributionTransactionManager")
	public void upsertProducts(List<BrandProduct> products) {
		for (BrandProduct product:products) {
			try {
				Assert.notNull(product.getSystemSourceId(), "The systemSourceId cannot be null.");
				Assert.hasText(product.getProductId(), "The productId is required.");
				Assert.hasText(product.getProductName(), "The productName is required.");
				Assert.hasText(product.getCategoryName(), "The categoryName is required.");
				Assert.hasText(product.getColor(), "The color is required.");
				Assert.hasText(product.getBrandName(), "The brandName is required.");
				mapper.upsertProduct(product);
				int id = mapper.getProductId(product.getProductId(), product.getSystemSourceId());
				product.setId(id);
				mapper.upsertJsonReferences(product.getId(),product.getJsonReferences());
			} catch (Exception e) {
				LOGGER.error("The product could be inserted/updated:" +product.getProductId() );
			}
		}	
		
	}
	
	@Override
	public void deleteInactiveProducts(Integer systemSourceId) {
		List<ProductJson> productJsons = mapper.listInactiveProducts(systemSourceId);
		List<Integer> productJsonIds = new ArrayList<>();
		List<Integer> productIds = new ArrayList<>();
		List<Integer> jsonIds = new ArrayList<>();
		if (productJsons != null && !productJsons.isEmpty()) {
			for (ProductJson productJson: productJsons) {
				if (!productJsonIds.contains(productJson.getId())) {
					productJsonIds.add(productJson.getId());
				}
				if (!productIds.contains(productJson.getProductId())) {
					productIds.add(productJson.getProductId());
				}
				if (!jsonIds.contains(productJson.getJsonId())) {
					jsonIds.add(productJson.getJsonId());
				}
			}		
		}
		mapper.deleteInactiveProductJson(productJsonIds);
		mapper.deleteInactiveJson(jsonIds);
		mapper.deleteInactiveProducts(systemSourceId, productIds);
		
	}

}
