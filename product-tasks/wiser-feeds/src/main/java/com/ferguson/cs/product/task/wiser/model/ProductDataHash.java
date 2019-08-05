package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Date;

public class ProductDataHash implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer productUniqueId;
	private String hashCode;
	private Date lastModifiedDate;

	public Integer getProductUniqueId() {
		return productUniqueId;
	}

	public void setProductUniqueId(Integer productUniqueId) {
		this.productUniqueId = productUniqueId;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
