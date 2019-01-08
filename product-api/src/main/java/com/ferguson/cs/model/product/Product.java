package com.ferguson.cs.model.product;

import java.util.List;

import com.ferguson.cs.model.manufacturer.Manufacturer;

public class Product {

	String productId;
	String title;
	String description;
	Manufacturer manufacturer;
	

	List<ProductItem> productItemList;
	
}
