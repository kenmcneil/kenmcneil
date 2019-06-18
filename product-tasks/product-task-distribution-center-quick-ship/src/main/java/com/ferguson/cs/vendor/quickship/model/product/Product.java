package com.ferguson.cs.vendor.quickship.model.product;

import java.io.Serializable;
import java.util.List;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;

public class Product implements Serializable {
	private static final long serialVersionUID = 2L;

	private Integer id;
	private ProductFamily family;
	private ProductFinish finish;
	private List<DistributionCenter> distributionCenterList;
	private Boolean isFreeShipping;
	private BigDecimal defaultPriceBookCost;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ProductFamily getFamily() {
		return family;
	}

	public void setFamily(ProductFamily family) {
		this.family = family;
	}

	public ProductFinish getFinish() {
		return finish;
	}

	public void setFinish(ProductFinish finish) {
		this.finish = finish;
	}

	public List<DistributionCenter> getDistributionCenterList() {
		return distributionCenterList;
	}

	public void setDistributionCenterList(List<DistributionCenter> distributionCenterList) {
		this.distributionCenterList = distributionCenterList;
	}

	public Boolean getFreeShipping() {
		return isFreeShipping;
	}

	public void setFreeShipping(Boolean freeShipping) {
		isFreeShipping = freeShipping;
	}

	public Double getDefaultPriceBookCost() {
		return defaultPriceBookCost;
	}

	public void setDefaultPriceBookCost(Double defaultPriceBookCost) {
		this.defaultPriceBookCost = defaultPriceBookCost;
	}
}
