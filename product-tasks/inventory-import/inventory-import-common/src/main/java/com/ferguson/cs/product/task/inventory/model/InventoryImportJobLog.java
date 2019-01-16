package com.ferguson.cs.product.task.inventory.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class InventoryImportJobLog implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Date lastUpdatedDate;
	private Integer vendorUid;
	private InventoryImportJobType jobType;
	private InventoryImportJobStatus status;
	private List<InventoryImportJobError> errors = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdateDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Integer getVendorUid() {
		return vendorUid;
	}

	public void setVendorUid(Integer vendorUid) {
		this.vendorUid = vendorUid;
	}

	public InventoryImportJobType getJobType() {
		return jobType;
	}

	public void setJobType(InventoryImportJobType jobType) {
		this.jobType = jobType;
	}

	public InventoryImportJobStatus getStatus() {
		return status;
	}

	public void setStatus(InventoryImportJobStatus status) {
		this.status = status;
	}

	public List<InventoryImportJobError> getErrors() {
		return errors;
	}

	public void setErrors(List<InventoryImportJobError> errors) {
		this.errors = errors;
	}
}
