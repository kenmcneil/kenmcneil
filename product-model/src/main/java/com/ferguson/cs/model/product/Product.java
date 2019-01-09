package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.util.List;

import com.ferguson.cs.model.manufacturer.Manufacturer;

public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	String productId;
	String title;
	String description;
	Manufacturer manufacturer;
	

	List<ProductItem> productItemList;
	
}
