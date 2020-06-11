package com.ferguson.cs.product.task.feipricefeed.model;

import java.io.Serializable;

public class FeiPriceData implements Serializable {
	private static final long serialVersionUID = 3L;

	private Integer uniqueId;
	private String mpid;
	private Double price;
	private String brand;
	private String status;
	private FeiPricingType feiPricingType;

	public String getMpid() {
		return mpid;
	}

	public void setMpid(String mpid) {
		this.mpid = mpid;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
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
				this.getStatus().equalsIgnoreCase(otherInstance.getStatus());
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
}
