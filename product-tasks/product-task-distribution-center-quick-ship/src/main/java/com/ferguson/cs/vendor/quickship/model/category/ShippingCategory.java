package com.ferguson.cs.vendor.quickship.model.category;

import java.io.Serializable;

public class ShippingCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer genericCategoryId;
	private Integer shippingCalculationId;
	private Integer shippingCalculationNameId;
	private Boolean hasFreeShippingPromo;
	private BigDecimal freeShippingPrice;

	public Integer getGenericCategoryId() {
		return genericCategoryId;
	}

	public void setGenericCategoryId(Integer genericCategoryId) {
		this.genericCategoryId = genericCategoryId;
	}

	public Integer getShippingCalculationId() {
		return shippingCalculationId;
	}

	public void setShippingCalculationId(Integer shippingCalculationId) {
		this.shippingCalculationId = shippingCalculationId;
	}

	public Integer getShippingCalculationNameId() {
		return shippingCalculationNameId;
	}

	public void setShippingCalculationNameId(Integer shippingCalculationNameId) {
		this.shippingCalculationNameId = shippingCalculationNameId;
	}

	public Boolean getHasFreeShippingPromo() {
		return hasFreeShippingPromo;
	}

	public void setHasFreeShippingPromo(Boolean hasFreeShippingPromo) {
		this.hasFreeShippingPromo = hasFreeShippingPromo;
	}

	public Double getFreeShippingPrice() {
		return freeShippingPrice;
	}

	public void setFreeShippingPrice(Double freeShippingPrice) {
		this.freeShippingPrice = freeShippingPrice;
	}
}
