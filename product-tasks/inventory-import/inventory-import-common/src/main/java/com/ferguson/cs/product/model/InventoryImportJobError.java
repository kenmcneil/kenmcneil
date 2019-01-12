package com.ferguson.cs.product.model;

import java.io.Serializable;

public class InventoryImportJobError implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer inventoryImportJobLogId;
	private String errorMessage;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getInventoryImportJobLogId() {
		return inventoryImportJobLogId;
	}

	public void setInventoryImportJobLogId(Integer inventoryImportJobLogId) {
		this.inventoryImportJobLogId = inventoryImportJobLogId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
