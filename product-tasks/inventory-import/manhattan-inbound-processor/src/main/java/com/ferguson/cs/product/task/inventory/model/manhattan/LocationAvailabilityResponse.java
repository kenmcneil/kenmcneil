package com.ferguson.cs.product.task.inventory.model.manhattan;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class LocationAvailabilityResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String itemId;
	private String locationId;
	private String status;
	private Integer statusCode;
	private Integer quantity;
	private Date nextAvailabilityDate;
	private Date transactionDateTime;
	private String viewName;
	private String viewId;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Date getNextAvailabilityDate() {
		return nextAvailabilityDate;
	}

	public void setNextAvailabilityDate(Date nextAvailabilityDate) {
		this.nextAvailabilityDate = nextAvailabilityDate;
	}

	public Date getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(Date transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
}
