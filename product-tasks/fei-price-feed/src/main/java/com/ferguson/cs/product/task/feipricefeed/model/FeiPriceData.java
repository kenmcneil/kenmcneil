package com.ferguson.cs.product.task.feipricefeed.model;

import java.io.Serializable;

public class FeiPriceData implements Serializable {
	private static final long serialVersionUID = 5L;

	private Integer uniqueId;
	private String mpid;
	//This is a string because sometimes we want to send things that aren't numbers
	private String price;
	private String brand;
	private String status;
	private FeiPricingType feiPricingType;
	private Double preferredVendorCost;
	private FeiPriceDataStatus feiPriceDataStatus;


	public String getMpid() {
		return mpid;
	}

	public void setMpid(String mpid) {
		this.mpid = mpid;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object other) {

		if(!(other instanceof  FeiPriceData)) {
			return false;
		}

		if(this == other) {
			return true;
		}

		FeiPriceData otherInstance = (FeiPriceData)other;

		return this.getMpid().equalsIgnoreCase(otherInstance.getMpid()) &&
				this.getBrand().equalsIgnoreCase(otherInstance.getBrand()) &&
				this.getPrice().equals(otherInstance.getPrice()) &&
				this.getStatus().equalsIgnoreCase(otherInstance.getStatus()) &&
				this.getPreferredVendorCost().equals(otherInstance.getPreferredVendorCost());
	}

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public FeiPricingType getFeiPricingType() {
		return feiPricingType;
	}

	public void setFeiPricingType(FeiPricingType feiPricingType) {
		this.feiPricingType = feiPricingType;
	}

	public Double getPreferredVendorCost() {
		return preferredVendorCost;
	}

	public void setPreferredVendorCost(Double preferredVendorCost) {
		this.preferredVendorCost = preferredVendorCost;
	}

	public FeiPriceDataStatus getFeiPriceDataStatus() {
		return feiPriceDataStatus;
	}

	public void setFeiPriceDataStatus(FeiPriceDataStatus feiPriceDataStatus) {
		this.feiPriceDataStatus = feiPriceDataStatus;
	}
}
