package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Date;

public class WiserPriceData implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer sku;
	private Integer channel;
	private Date effectiveDate;
	private Double regularPrice;

	public WiserPriceData() {

	}

	public WiserPriceData(WiserPriceData source) {
		this.sku = source.sku;
		this.channel = source.channel;
		this.effectiveDate = source.effectiveDate;
		this.regularPrice = source.regularPrice;

	}

	public Integer getSku() {
		return sku;
	}

	public void setSku(Integer sku) {
		this.sku = sku;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Double getRegularPrice() {
		return regularPrice;
	}

	public void setRegularPrice(Double regularPrice) {
		this.regularPrice = regularPrice;
	}
}
