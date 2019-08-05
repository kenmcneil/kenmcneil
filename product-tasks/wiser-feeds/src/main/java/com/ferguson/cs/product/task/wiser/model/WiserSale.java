package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Date;

public class WiserSale implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer productUniqueId;
	private Date startDate;
	private Date endDate;
	private Date modifiedDate;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProductUniqueId() {
		return productUniqueId;
	}

	public void setProductUniqueId(Integer productUniqueId) {
		this.productUniqueId = productUniqueId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
