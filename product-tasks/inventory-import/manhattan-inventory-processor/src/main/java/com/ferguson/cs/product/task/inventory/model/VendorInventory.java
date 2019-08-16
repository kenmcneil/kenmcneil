package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;

public class VendorInventory implements Serializable {
	private static final long serialVersionUID = 2L;

	private String mpid;
	private String location;
	private Integer quantity;
	private Integer manhattanInventoryJobId;

	public String getMpid() {
		return mpid;
	}

	public void setMpid(String mpid) {
		this.mpid = mpid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getManhattanInventoryJobId() {
		return manhattanInventoryJobId;
	}

	public void setManhattanInventoryJobId(Integer manhattanInventoryJobId) {
		this.manhattanInventoryJobId = manhattanInventoryJobId;
	}
}