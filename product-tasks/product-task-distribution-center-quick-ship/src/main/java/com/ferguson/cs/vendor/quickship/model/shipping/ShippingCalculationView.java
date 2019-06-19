package com.ferguson.cs.vendor.quickship.model.shipping;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShippingCalculationView implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer shippingCalculationId;
	private Integer shippingCalculationNameId;
	private Boolean hasFreeShippingPromo;
	private BigDecimal freeShippingPrice;

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

	public BigDecimal getFreeShippingPrice() {
		return freeShippingPrice;
	}

	public void setFreeShippingPrice(BigDecimal freeShippingPrice) {
		this.freeShippingPrice = freeShippingPrice;
	}

}
