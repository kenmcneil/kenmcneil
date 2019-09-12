package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;

public class ProductRevenueCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer productUniqueId;
	private RevenueCategory revenueCategory;

	public Integer getProductUniqueId() {
		return productUniqueId;
	}

	public void setProductUniqueId(Integer productUniqueId) {
		this.productUniqueId = productUniqueId;
	}

	public RevenueCategory getRevenueCategory() {
		return revenueCategory;
	}

	public void setRevenueCategory(RevenueCategory revenueCategory) {
		this.revenueCategory = revenueCategory;
	}
}
