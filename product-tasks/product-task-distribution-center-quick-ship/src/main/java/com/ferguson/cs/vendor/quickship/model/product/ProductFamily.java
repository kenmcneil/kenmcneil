package com.ferguson.cs.vendor.quickship.model.product;

import java.io.Serializable;

public class ProductFamily implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String productId;
	private Manufacturer manufacturer;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
}
