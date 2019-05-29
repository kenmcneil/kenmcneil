package com.ferguson.cs.vendor.quickship.model.vendor;

import java.io.Serializable;

import com.ferguson.cs.vendor.quickship.model.product.Product;

/**
 * Object used to reference the assocation between a distribution center and product for Quick Ship
 *
 * @author francisco.cha
 *
 */
public class DistributionCenterProductQuickShip implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private DistributionCenter distributionCenter;
	private Product product;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DistributionCenter getDistributionCenter() {
		return distributionCenter;
	}

	public void setDistributionCenter(DistributionCenter distributionCenter) {
		this.distributionCenter = distributionCenter;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
