package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Date;

public class WiserPerformanceData implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer sku;
	private Date transactionDate;
	private Integer grossUnits;
	private Integer grossOrders;
	private Double grossRevenue;
	private Integer channel;
	private Boolean ncr;
	private Integer marketplaceId;

	public Integer getSku() {
		return sku;
	}

	public void setSku(Integer sku) {
		this.sku = sku;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getGrossUnits() {
		return grossUnits;
	}

	public void setGrossUnits(Integer grossUnits) {
		this.grossUnits = grossUnits;
	}

	public Double getGrossRevenue() {
		return grossRevenue;
	}

	public void setGrossRevenue(Double grossRevenue) {
		this.grossRevenue = grossRevenue;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Integer getMarketplaceId() {
		return marketplaceId;
	}

	public void setMarketplaceId(Integer marketplaceId) {
		this.marketplaceId = marketplaceId;
	}

	public Integer getGrossOrders() {
		return grossOrders;
	}

	public void setGrossOrders(Integer grossOrders) {
		this.grossOrders = grossOrders;
	}

	public Boolean getNcr() {
		return ncr;
	}

	public void setNcr(Boolean ncr) {
		this.ncr = ncr;
	}
}
