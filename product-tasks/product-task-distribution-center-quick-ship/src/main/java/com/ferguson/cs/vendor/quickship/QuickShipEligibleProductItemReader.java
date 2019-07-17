package com.ferguson.cs.vendor.quickship;

import java.util.List;

import org.springframework.batch.item.ItemReader;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.service.product.ProductService;

public class QuickShipEligibleProductItemReader implements ItemReader<List<Product>> {
	private final ProductService productService;
	private int pageNumber = 0;

	public QuickShipEligibleProductItemReader(ProductService productService) {
		this.productService = productService;
	}

	@Override
	public List<Product> read() {
		pageNumber++;
		List<Product> quickShipEligibleProductList = productService.getQuickShipProductList(pageNumber);

		if (quickShipEligibleProductList.isEmpty()) {
			return null;
		}

		return quickShipEligibleProductList;
	}
}
