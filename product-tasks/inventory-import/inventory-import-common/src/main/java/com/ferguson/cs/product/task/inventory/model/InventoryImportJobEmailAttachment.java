package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;

public class InventoryImportJobEmailAttachment implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer inventoryImportJobLogId;
	private String filename;
	private Boolean wasSuccessful;

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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Boolean getWasSuccessful() {
		return wasSuccessful;
	}

	public void setWasSuccessful(Boolean wasSuccessful) {
		this.wasSuccessful = wasSuccessful;
	}
}
