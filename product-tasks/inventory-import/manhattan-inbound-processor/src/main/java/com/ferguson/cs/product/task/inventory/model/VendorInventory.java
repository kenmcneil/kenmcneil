package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;

public class VendorInventory implements Serializable {
	private static final long serialVersionUID = 1L;

	private String sku;
	private String location;
	private Integer quantity;
	private String transactionNumber;

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
}
