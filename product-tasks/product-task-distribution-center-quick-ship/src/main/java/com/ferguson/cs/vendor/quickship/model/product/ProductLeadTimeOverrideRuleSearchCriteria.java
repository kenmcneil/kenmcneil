package com.ferguson.cs.vendor.quickship.model.product;

import java.io.Serializable;
import java.util.List;

public class ProductLeadTimeOverrideRuleSearchCriteria implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer productId;
	private List<ProductLeadTimeOverrideType> typeList;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public List<ProductLeadTimeOverrideType> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<ProductLeadTimeOverrideType> typeList) {
		this.typeList = typeList;
	}
}
