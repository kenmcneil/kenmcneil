package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;
import java.util.List;

public class ElectroluxInventoryResponse implements Serializable {
	private static final long serialVersionUID = 2L;

	private String status;
	private List<InventoryRecord> inventoryResponse;
	private ErrorDetail errorDetail;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<InventoryRecord> getInventoryResponse() {
		return inventoryResponse;
	}

	public void setInventoryResponse(List<InventoryRecord> inventoryResponse) {
		this.inventoryResponse = inventoryResponse;
	}

	public ErrorDetail getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(ErrorDetail errorDetail) {
		this.errorDetail = errorDetail;
	}
}
