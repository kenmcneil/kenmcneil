package com.ferguson.cs.product.task.feipriceupdate.model;

import java.io.Serializable;

public class PriceBookSync implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer jobId;
	private String productId;
	private String manufacturer;
	private String finish;
	private Integer uniqueId;
	private Double listPrice;
	private Integer priceBookId;
	private Double cost;
	private Boolean isDelete;
	private Boolean hasError;
	private String syncErrorReason;
	private ProductSyncErrorType syncErrorType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Double getListPrice() {
		return listPrice;
	}

	public void setListPrice(Double listPrice) {
		this.listPrice = listPrice;
	}

	public Integer getPriceBookId() {
		return priceBookId;
	}

	public void setPriceBookId(Integer priceBookId) {
		this.priceBookId = priceBookId;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}

	public String getSyncErrorReason() {
		return syncErrorReason;
	}

	public void setSyncErrorReason(String syncErrorReason) {
		this.syncErrorReason = syncErrorReason;
	}

	public ProductSyncErrorType getSyncErrorType() {
		return syncErrorType;
	}

	public void setSyncErrorType(ProductSyncErrorType syncErrorType) {
		this.syncErrorType = syncErrorType;
	}
}
