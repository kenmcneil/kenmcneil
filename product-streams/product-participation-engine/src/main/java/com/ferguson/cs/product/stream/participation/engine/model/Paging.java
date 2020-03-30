package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;

public class Paging implements Serializable {
	private static final long serialVersionUID = 1L;

	private int page;
	private int pageSize;
	private int total;
	private int pages;

	public Paging() {
	}

	public Paging(int page, int pageSize, int total, int pages) {
		this.page = page;
		this.pageSize = pageSize;
		this.total = total;
		this.pages = pages;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

}
