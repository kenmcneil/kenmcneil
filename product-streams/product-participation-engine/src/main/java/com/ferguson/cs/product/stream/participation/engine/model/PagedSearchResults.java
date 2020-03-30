package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class PagedSearchResults<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Paging paging;

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
	private List<T> data;

	public PagedSearchResults() {
	}

	public PagedSearchResults(List<T> data) {
		this.data = data;
		this.paging = new Paging(1, Integer.MAX_VALUE, data.size(), 1);
	}

	public PagedSearchResults(Paging paging, List<T> data) {
		this.paging = paging;
		this.data = data;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
