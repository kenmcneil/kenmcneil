package com.ferguson.cs.vendor.quickship.model.product;

import java.io.Serializable;

public class ProductLeadTimeOverrideRule implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private ProductLeadTimeOverrideType type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProductLeadTimeOverrideType getType() {
		return type;
	}

	public void setType(ProductLeadTimeOverrideType type) {
		this.type = type;
	}
}
