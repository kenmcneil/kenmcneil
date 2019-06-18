package com.ferguson.cs.vendor.quickship.service.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.ferguson.cs.test.utilities.ValueUtils;
import com.ferguson.cs.vendor.quickship.model.shipping.ShippingCalculationView;
import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.service.product.ProductDao;
import com.ferguson.cs.vendor.quickship.service.product.ProductServiceImpl;
import com.ferguson.cs.vendor.quickship.service.shipping.ShippingService;

public class ProductServiceTest {

	private static final int BUILD_SITE_ID = 82;
	private static final int BUILD_STORE_ID = 248;
	private static final int STANDARD_DELIVERY_CALCULATION_NAME_ID = 68;

	@Mock
	ShippingService shippingService;

	@Mock
	ProductDao productDao;

	@InjectMocks
	ProductServiceImpl productService;


	@Before
	public void setUpBeforeTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsFreeShipping_freeShippingFlag() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(true);
		product.setDefaultPriceBookCost(BigDecimal.valueOf(9.00));

		ShippingCalculationView storeShippingCalculationView = new ShippingCalculationView();
		storeShippingCalculationView.setFreeShippingPrice(BigDecimal.valueOf(9000.00));
		storeShippingCalculationView.setHasFreeShippingPromo(true);
		storeShippingCalculationView.setShippingCalculationId(22);
		storeShippingCalculationView.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);
		storeShippingCalculationView.setGenericCategoryRootId(2);
		boolean freeShipping = productService.isFreeShipping(product,storeShippingCalculationView);

		assertThat(freeShipping).isTrue();
	}

	@Test
	public void testIsFreeShipping_storeCategoryPriceThreshold() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(false);
		product.setDefaultPriceBookCost(BigDecimal.valueOf(7.77));
		ShippingCalculationView storeShippingCalculationView = new ShippingCalculationView();
		storeShippingCalculationView.setFreeShippingPrice(BigDecimal.valueOf(6.66));
		storeShippingCalculationView.setHasFreeShippingPromo(true);
		storeShippingCalculationView.setShippingCalculationId(22);
		storeShippingCalculationView.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);
		storeShippingCalculationView.setGenericCategoryRootId(2);

		when(shippingService.getStoreShippingCalculationView(any(), any(), any())).thenReturn(storeShippingCalculationView);

		boolean freeShipping = productService.isFreeShipping(product,storeShippingCalculationView);

		assertThat(freeShipping).isTrue();
	}

	@Test
	public void testIsFreeShipping_productCategoryPriceThreshold() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(false);
		product.setDefaultPriceBookCost(BigDecimal.valueOf(7.77));

		ShippingCalculationView storeShippingCalculationView = new ShippingCalculationView();
		storeShippingCalculationView.setFreeShippingPrice(BigDecimal.valueOf(66.66));
		storeShippingCalculationView.setHasFreeShippingPromo(true);
		storeShippingCalculationView.setShippingCalculationId(22);
		storeShippingCalculationView.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);
		storeShippingCalculationView.setGenericCategoryRootId(2);

		ShippingCalculationView productShippingCalculationView = new ShippingCalculationView();
		productShippingCalculationView.setFreeShippingPrice(BigDecimal.valueOf(6.66));
		productShippingCalculationView.setHasFreeShippingPromo(true);
		productShippingCalculationView.setShippingCalculationId(222);
		productShippingCalculationView.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);
		storeShippingCalculationView.setGenericCategoryRootId(2);

		when(shippingService.getStoreShippingCalculationView(any(), any(), any())).thenReturn(storeShippingCalculationView);
		when(shippingService.getUniqueIdShippingCalculationView(storeShippingCalculationView.getGenericCategoryRootId(), product
				.getId(), storeShippingCalculationView
				.getShippingCalculationNameId())).thenReturn(productShippingCalculationView);

		boolean freeShipping = productService.isFreeShipping(product,storeShippingCalculationView);

		assertThat(freeShipping).isTrue();
	}

	@Test
	public void testIsFreeShipping_notFreeShipping() {
		Product product = ValueUtils.getRandomValue(Product.class);
		product.setFreeShipping(false);
		product.setDefaultPriceBookCost(BigDecimal.valueOf(7.77));

		ShippingCalculationView storeShippingCalculationView = new ShippingCalculationView();
		storeShippingCalculationView.setFreeShippingPrice(BigDecimal.valueOf(66.66));
		storeShippingCalculationView.setHasFreeShippingPromo(true);
		storeShippingCalculationView.setShippingCalculationId(22);
		storeShippingCalculationView.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);
		storeShippingCalculationView.setGenericCategoryRootId(2);

		ShippingCalculationView productShippingCalculationView = new ShippingCalculationView();
		productShippingCalculationView.setFreeShippingPrice(BigDecimal.valueOf(7.86));
		productShippingCalculationView.setHasFreeShippingPromo(true);
		productShippingCalculationView.setShippingCalculationId(222);
		productShippingCalculationView.setShippingCalculationNameId(STANDARD_DELIVERY_CALCULATION_NAME_ID);
		storeShippingCalculationView.setGenericCategoryRootId(2);

		when(shippingService.getStoreShippingCalculationView(any(), any(), any())).thenReturn(storeShippingCalculationView);
		when(shippingService.getUniqueIdShippingCalculationView(storeShippingCalculationView.getGenericCategoryRootId(), product
				.getId(), storeShippingCalculationView
				.getShippingCalculationNameId())).thenReturn(productShippingCalculationView);

		boolean freeShipping = productService.isFreeShipping(product,storeShippingCalculationView);

		assertThat(freeShipping).isFalse();
	}


}
