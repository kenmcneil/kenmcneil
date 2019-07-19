package com.ferguson.cs.vendor.quickship.service.product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ferguson.cs.vendor.quickship.model.category.ShippingCalculationView;
import com.ferguson.cs.vendor.quickship.model.product.Product;
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

	//This is the generic category root id for build. It's not expected to change...
	private static final int DEFAULT_GENERIC_CATEGORY_ROOT_ID = 2;

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
	public boolean isFreeShipping(Product product, ShippingCalculationView storeShippingCalculationView) {
		//Free shipping flag overrides all other logic if set to true
		if (product.getFreeShipping()) {
			return true;
		}
		boolean isFreeShipping = false;



		//Check if product's price is over free shipping threshold. If not, check if there is a product specific override.
		if (storeShippingCalculationView != null && product.getDefaultPriceBookCost() != null) {

			isFreeShipping = isPriceOverFreeThreshold(storeShippingCalculationView, product.getDefaultPriceBookCost());

			if (!isFreeShipping) {
				ShippingCalculationView productShippingCalculationView = categoryService
						.getUniqueIdShippingCalculationView(DEFAULT_GENERIC_CATEGORY_ROOT_ID, product.getId(),storeShippingCalculationView.getShippingCalculationNameId());

				if (productShippingCalculationView != null) {
					isFreeShipping = isPriceOverFreeThreshold(productShippingCalculationView, product
							.getDefaultPriceBookCost());
				}
			}
		}
		return isFreeShipping;
	}

	private boolean isPriceOverFreeThreshold(ShippingCalculationView shippingCalculationView, BigDecimal price) {
		return Boolean.TRUE.equals(shippingCalculationView.getHasFreeShippingPromo()) &&
				shippingCalculationView.getFreeShippingPrice() != null &&
				price != null &&
				price.compareTo(shippingCalculationView.getFreeShippingPrice()) > 0;
	}
}
