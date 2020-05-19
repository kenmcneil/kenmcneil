package com.ferguson.cs.product.task.feipricefeed.model;

import java.io.Serializable;

public class DeprioritizedBrandView implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer manufacturerId;
	private String manufacturerName;

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}
}
