package com.ferguson.cs.product.task.brand.model;

import java.io.Serializable;
import java.util.Date;

public class SystemSource implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String sourceName;
	private Integer activeProductsFetched;
	private Integer obsoleteProductsFetched;
	private Date activeProductsLastUpdated;
	private Date obsoleteProductsLastUpdated;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public Integer getActiveProductsFetched() {
		return activeProductsFetched;
	}

	public void setActiveProductsFetched(Integer activeProductsFetched) {
		this.activeProductsFetched = activeProductsFetched;
	}

	public Integer getObsoleteProductsFetched() {
		return obsoleteProductsFetched;
	}

	public void setObsoleteProductsFetched(Integer obsoleteProductsFetched) {
		this.obsoleteProductsFetched = obsoleteProductsFetched;
	}

	public Date getActiveProductsLastUpdated() {
		return activeProductsLastUpdated;
	}

	public void setActiveProductsLastUpdated(Date activeProductsLastUpdated) {
		this.activeProductsLastUpdated = activeProductsLastUpdated;
	}

	public Date getObsoleteProductsLastUpdated() {
		return obsoleteProductsLastUpdated;
	}

	public void setObsoleteProductsLastUpdated(Date obsoleteProductsLastUpdated) {
		this.obsoleteProductsLastUpdated = obsoleteProductsLastUpdated;
	}

}
