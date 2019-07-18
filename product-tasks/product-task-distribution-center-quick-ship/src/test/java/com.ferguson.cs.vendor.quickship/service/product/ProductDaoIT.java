package com.ferguson.cs.vendor.quickship.service.product;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideType;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;
import com.ferguson.cs.vendor.quickship.service.BaseVendorQuickShipTest;

public class ProductDaoIT extends BaseVendorQuickShipTest {

	@Autowired
	private ProductDao productDao;

	@Test
	@Ignore("Refactor to use seeded data")
	public void testGetQuickShipEligibleProduct() {
		int pageSize = 10;
		QuickshipEligibleProductSearchCriteria criteria = new QuickshipEligibleProductSearchCriteria();
		criteria.setOffset(0);
		criteria.setPageSize(pageSize);
		criteria.setVendorIdList(Arrays.asList(112));

		List<Product> retrievedList = productDao.getQuickShipEligibleProduct(criteria);

		assertTrue(!retrievedList.isEmpty());
		assertTrue(retrievedList.size() > 0 && retrievedList.size() <= pageSize);
	}
}
