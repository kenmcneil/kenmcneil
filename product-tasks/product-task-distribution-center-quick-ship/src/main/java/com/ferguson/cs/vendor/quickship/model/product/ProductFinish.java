package com.ferguson.cs.vendor.quickship.model.product;

import java.io.Serializable;

public class ProductFinish implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
