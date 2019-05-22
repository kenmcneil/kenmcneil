package com.ferguson.cs.vendor.quickship.service.product;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductDao productDao;

	@Value("${vendor-quickship.batchSize:1000}")
	private int batchSize;

	@Value("${vendor-quickship.vendorId:112}")
	private int vendorId;

	public ProductServiceImpl(ProductDao productDao) {
		this.productDao = productDao;
	}

	@Override
	public List<Product> getQuickShipProductList(int pageNumber) {
		Assert.isTrue(pageNumber > 0, "Page number must be > 0");

		int offset = (pageNumber - 1) * batchSize;

		QuickshipEligibleProductSearchCriteria criteria = new QuickshipEligibleProductSearchCriteria();
		criteria.setOffset(offset);
		criteria.setPageSize(batchSize);
		criteria.setVendorIdList(Arrays.asList(vendorId));

		return productDao.getQuickShipEligibleProduct(criteria);
	}

	@Override
	public List<ProductLeadTimeOverrideRule> getLeadTimeOverrideRuleList(
			ProductLeadTimeOverrideRuleSearchCriteria criteria) {
		return productDao.getProductLeadTimeOverrideRule(criteria);
	}
}
