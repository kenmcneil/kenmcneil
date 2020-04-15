package com.ferguson.cs.vendor.quickship.service.product;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;

@Repository
public class ProductDaoImpl implements ProductDao {
	private final ProductMapper productMapper;

	public ProductDaoImpl(ProductMapper productMapper) {
		this.productMapper = productMapper;
	}

	@Override
	public List<Product> getQuickShipEligibleProduct(QuickshipEligibleProductSearchCriteria criteria) {
		return productMapper.getQuickShipEligibleProduct(criteria);
	}

	@Override
	public void updateProductModified(Product product) {
		productMapper.updateProductModified(product);
	}

	@Override
	public void truncateProductPreferredVendorQuickShip() {
		productMapper.truncateProductPreferredVendorQuickShip();
	}

	@Override
	public void populateProductPreferredVendorQuickShip() {
		productMapper.copyProductPreferredVendorTableForQuickShip();
	}
}
