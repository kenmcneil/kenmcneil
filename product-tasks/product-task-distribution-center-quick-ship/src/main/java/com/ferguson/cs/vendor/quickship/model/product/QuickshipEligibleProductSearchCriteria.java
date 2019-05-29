package com.ferguson.cs.vendor.quickship.model.product;

import java.io.Serializable;
import java.util.List;

import com.ferguson.cs.vendor.quickship.model.PaginationCriteria;

public class QuickshipEligibleProductSearchCriteria extends PaginationCriteria implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Integer> vendorIdList;

	public List<Integer> getVendorIdList() {
		return vendorIdList;
	}

	public void setVendorIdList(List<Integer> vendorIdList) {
		this.vendorIdList = vendorIdList;
	}
}
