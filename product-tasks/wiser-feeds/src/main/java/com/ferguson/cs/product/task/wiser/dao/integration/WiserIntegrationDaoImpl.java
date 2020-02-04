package com.ferguson.cs.product.task.wiser.dao.integration;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

@Repository
public class WiserIntegrationDaoImpl implements WiserIntegrationDao {

	private WiserIntegrationMapper wiserIntegrationMapper;

	@Autowired
	public void setWiserIntegrationMapper(WiserIntegrationMapper wiserIntegrationMapper) {
		this.wiserIntegrationMapper = wiserIntegrationMapper;
	}

	@Override
	public List<WiserSale> getActiveOrModifiedWiserSales(Date date) {
		return wiserIntegrationMapper.getActiveOrModifiedWiserSales(date);
	}

	@Override
	public List<ProductDataHash> getAllProductDataHashes() {
		return wiserIntegrationMapper.getAllProductDataHashes();
	}

	@Override
	public void truncateProductDataHashes() {
		wiserIntegrationMapper.truncateProductDataHashes();
	}

	@Override
	public Map<Integer, ProductConversionBucket> getProductConversionBuckets() {
		return wiserIntegrationMapper.getProductConversionBuckets().stream()
				.collect(Collectors.toMap(ProductConversionBucket::getProductUniqueId, Function
						.identity()));
	}

	@Override
	public ProductConversionBucket getProductConversionBucket(Integer productUniqueId) {
		return wiserIntegrationMapper.getProductConversionBucket(productUniqueId);
	}

	@Override
	public void populateProductRevenueCategorization() {
		wiserIntegrationMapper.populateProductRevenueCategorization();
	}
}
