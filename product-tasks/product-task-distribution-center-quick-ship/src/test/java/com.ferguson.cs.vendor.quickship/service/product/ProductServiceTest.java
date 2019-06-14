package com.ferguson.cs.vendor.quickship.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.ferguson.cs.test.utilities.ValueUtils;
import com.ferguson.cs.vendor.quickship.model.category.ShippingCategory;
import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.service.category.CategoryService;

public class ProductServiceTest {

	private static final int STANDARD_DELIVERY_CALCULATION_NAME_ID = 68;

	@Mock
	CategoryService categoryService;

	@Mock
	ProductDao productDao;

	@InjectMocks
	ProductServiceImpl productService;


	@Before
	public void setUpBeforeTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testProductIsFreeShipping_freeShippingFlag() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(true);

		boolean freeShipping = productService.productIsFreeShipping(product);

		assertThat(freeShipping).isTrue();
	}

	@Test
	public void testProductIsFreeShipping_storeCategoryPriceThreshold() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(false);
		product.setDefaultPriceBookCost(7.77);
		ShippingCategory storeShippingCategory = new ShippingCategory();
		storeShippingCategory.setFreeShippingPrice(6.66);
		storeShippingCategory.setGenericCategoryId(1);
		storeShippingCategory.setHasFreeShippingPromo(true);
		storeShippingCategory.setShippingCalculationId(22);
		storeShippingCategory.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);

		when(categoryService.getStoreShippingCategory(any(), any(), any())).thenReturn(storeShippingCategory);

		boolean freeShipping = productService.productIsFreeShipping(product);

		assertThat(freeShipping).isTrue();
	}

	@Test
	public void testProductIsFreeShipping_productCategoryPriceThreshold() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(false);
		product.setDefaultPriceBookCost(7.77);

		ShippingCategory storeShippingCategory = new ShippingCategory();
		storeShippingCategory.setFreeShippingPrice(66.66);
		storeShippingCategory.setGenericCategoryId(1);
		storeShippingCategory.setHasFreeShippingPromo(true);
		storeShippingCategory.setShippingCalculationId(22);
		storeShippingCategory.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);

		ShippingCategory productShippingCategory = new ShippingCategory();
		productShippingCategory.setFreeShippingPrice(6.66);
		productShippingCategory.setGenericCategoryId(1);
		productShippingCategory.setHasFreeShippingPromo(true);
		productShippingCategory.setShippingCalculationId(222);
		productShippingCategory.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);

		when(categoryService.getStoreShippingCategory(any(), any(), any())).thenReturn(storeShippingCategory);
		when(categoryService.getUniqueIdShippingCategory(storeShippingCategory.getGenericCategoryId(), product
				.getId(), storeShippingCategory.getShippingCalculationNameId())).thenReturn(productShippingCategory);

		boolean freeShipping = productService.productIsFreeShipping(product);

		assertThat(freeShipping).isTrue();
	}

	@Test
	public void testProductIsFreeShipping_notFreeShipping() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(false);
		product.setDefaultPriceBookCost(7.77);

		ShippingCategory storeShippingCategory = new ShippingCategory();
		storeShippingCategory.setFreeShippingPrice(66.66);
		storeShippingCategory.setGenericCategoryId(1);
		storeShippingCategory.setHasFreeShippingPromo(true);
		storeShippingCategory.setShippingCalculationId(22);
		storeShippingCategory.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);

		ShippingCategory productShippingCategory = new ShippingCategory();
		productShippingCategory.setFreeShippingPrice(7.86);
		productShippingCategory.setGenericCategoryId(1);
		productShippingCategory.setHasFreeShippingPromo(true);
		productShippingCategory.setShippingCalculationId(222);
		productShippingCategory.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);

		when(categoryService.getStoreShippingCategory(any(), any(), any())).thenReturn(storeShippingCategory);
		when(categoryService.getUniqueIdShippingCategory(storeShippingCategory.getGenericCategoryId(), product
				.getId(), storeShippingCategory.getShippingCalculationNameId())).thenReturn(productShippingCategory);

		boolean freeShipping = productService.productIsFreeShipping(product);

		assertThat(freeShipping).isFalse();
	}


}
