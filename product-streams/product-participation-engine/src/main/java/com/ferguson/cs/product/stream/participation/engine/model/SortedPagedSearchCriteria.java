package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;

public abstract class SortedPagedSearchCriteria<S extends SortColumn>
		implements Serializable {

	private static final long serialVersionUID = 1L;

	private int page;
	private int pageSize;
	private S sortColumn;
	private SortOrder sortOrder;

	public SortedPagedSearchCriteria() {
	}

	public SortedPagedSearchCriteria(int page, int pageSize, S sortColumn, SortOrder sortOrder) {
		this.page = page;
		this.pageSize = pageSize;
		this.sortColumn = sortColumn;
		this.sortOrder = sortOrder;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public S getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(S sortColumn) {
		this.sortColumn = sortColumn;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
}
