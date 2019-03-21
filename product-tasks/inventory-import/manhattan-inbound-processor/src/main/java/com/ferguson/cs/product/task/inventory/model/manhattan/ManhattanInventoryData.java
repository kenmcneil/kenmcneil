package com.ferguson.cs.product.task.inventory.model.manhattan;

import java.io.Serializable;
import java.util.List;

public class ManhattanInventoryData implements Serializable{
	private static final long serialVersionUID = 1L;

	private String transactionNumber;
	private String transactionType;
	private List<LocationAvailabilityResponse> locationAvailabilityResponse;
	private Integer count;
	private Integer pageNumber;

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public List<LocationAvailabilityResponse> getLocationAvailabilityResponse() {
		return locationAvailabilityResponse;
	}

	public void setLocationAvailabilityResponse(List<LocationAvailabilityResponse> locationAvailabilityResponse) {
		this.locationAvailabilityResponse = locationAvailabilityResponse;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
}
