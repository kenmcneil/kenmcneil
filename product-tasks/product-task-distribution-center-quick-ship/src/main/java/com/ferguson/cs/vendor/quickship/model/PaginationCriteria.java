package com.ferguson.cs.vendor.quickship.model;

import java.io.Serializable;

public class PaginationCriteria implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer offset;
	private Integer pageSize;

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
