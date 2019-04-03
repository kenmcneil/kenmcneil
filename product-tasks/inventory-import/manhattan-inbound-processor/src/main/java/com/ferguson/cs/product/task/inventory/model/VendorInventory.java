package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;
import java.util.Date;

public class VendorInventory implements Serializable {
	private static final long serialVersionUID = 1L;

	private String mpn;
	private String location;
	private String status;
	private Integer quantity;
	private String jobKey;

	public String getMpn() {
		return mpn;
	}

	public void setMpn(String mpn) {
		this.mpn = mpn;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}
}
