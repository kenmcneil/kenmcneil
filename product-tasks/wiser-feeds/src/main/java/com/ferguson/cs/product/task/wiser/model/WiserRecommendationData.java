package com.ferguson.cs.product.task.wiser.model;

import java.io.Serializable;

public class WiserRecommendationData implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer uniqueId;
	private Integer pricebookId;
	private Double cost;
	private Double oldCost;

	public Integer getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(Integer uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Integer getPricebookId() {
		return pricebookId;
	}

	public void setPricebookId(Integer pricebookId) {
		this.pricebookId = pricebookId;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getOldCost() {
		return oldCost;
	}

	public void setOldCost(Double oldCost) {
		this.oldCost = oldCost;
	}
}
