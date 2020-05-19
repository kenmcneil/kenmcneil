package com.ferguson.cs.product.task.feipricefeed.model;

import java.io.Serializable;

public class FeiImapProductData implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer productUniqueId;
	private String mpn;
	private Double price;

	public Integer getProductUniqueId() {
		return productUniqueId;
	}

	public void setProductUniqueId(Integer productUniqueId) {
		this.productUniqueId = productUniqueId;
	}

	public String getMpn() {
		return mpn;
	}

	public void setMpn(String mpn) {
		this.mpn = mpn;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}
}
