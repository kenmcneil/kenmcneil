package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;
import java.util.Date;

public class WiserPerformanceData implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer transactionId;
	private Integer sku;
	private Date transactionDate;
	private Integer units;
	private Double unitPrice;
	private Double shipPrice;
	private Double revenue;
	private Integer channel;
	private Integer marketPlaceId;

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

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

	public Integer getUnits() {
		return units;
	}

	public void setUnits(Integer units) {
		this.units = units;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getShipPrice() {
		return shipPrice;
	}

	public void setShipPrice(Double shipPrice) {
		this.shipPrice = shipPrice;
	}

	public Double getRevenue() {
		return revenue;
	}

	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Integer getMarketPlaceId() {
		return marketPlaceId;
	}

	public void setMarketPlaceId(Integer marketPlaceId) {
		this.marketPlaceId = marketPlaceId;
	}
}
