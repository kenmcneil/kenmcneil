package com.ferguson.cs.vendor.quickship.service.product;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ferguson.cs.vendor.quickship.model.category.ShippingCategory;
import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.product.QuickshipEligibleProductSearchCriteria;
import com.ferguson.cs.vendor.quickship.service.category.CategoryService;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductDao productDao;
	private final CategoryService categoryService;

	@Value("${distribution-center-quick-ship.batch-size:1000}")
	private int batchSize;

	@Value("${distribution-center-quick-ship.vendor-id:112}")
	private int vendorId;

	private static final int BUILD_SITE_ID = 82;
	private static final int BUILD_STORE_ID = 248;
	private static final int STANDARD_DELIVERY_CALCULATION_NAME_ID = 68;

	public ProductServiceImpl(ProductDao productDao, CategoryService categoryService) {
		this.productDao = productDao;
		this.categoryService = categoryService;
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

	@Override
	public boolean productIsFreeShipping(Product product) {
		if (product.getFreeShipping()) {
			return true;
		}
		boolean isFreeShipping = false;
		//Get build's shipping category
		ShippingCategory storeShippingCategory = categoryService
				.getStoreShippingCategory(BUILD_SITE_ID, BUILD_STORE_ID, STANDARD_DELIVERY_CALCULATION_NAME_ID);


		//Check if product's price is over free shipping threshold. If not, check if there is a product specific override.
		if (storeShippingCategory != null && product.getDefaultPriceBookCost() != null) {

			isFreeShipping = isPriceOverFreeThreshold(storeShippingCategory, product.getDefaultPriceBookCost());


			if (!isFreeShipping) {
				ShippingCategory productShippingCategory = categoryService
						.getUniqueIdShippingCategory(storeShippingCategory.getGenericCategoryId(), product.getId(),STANDARD_DELIVERY_CALCULATION_NAME_ID);

				if (productShippingCategory != null) {
					isFreeShipping = isPriceOverFreeThreshold(productShippingCategory, product
							.getDefaultPriceBookCost());

					if(isFreeShipping) {
						System.out.println("foo");
					}
				}
			}
		}
		return isFreeShipping;
	}

	private boolean isPriceOverFreeThreshold(ShippingCategory shippingCategory, Double price) {
		return Boolean.TRUE.equals(shippingCategory.getHasFreeShippingPromo()) &&
				shippingCategory.getFreeShippingPrice() != null &&
				price > shippingCategory.getFreeShippingPrice();
	}
}
