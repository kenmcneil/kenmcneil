package com.ferguson.cs.vendor.quickship.model.vendor;

import java.io.Serializable;

import com.ferguson.cs.vendor.quickship.model.PaginationCriteria;

/**
 * Object for searching for a Quick Ship distribution center for the provided product information
 *
 * @author francisco.cha
 *
 */
public class QuickShipDistributionCenterSearchCriteria extends PaginationCriteria implements Serializable {
	private static final long serialVersionUID = 1L;

	private String productId;
	private String manufacturerName;
	private String finishDescription;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getFinishDescription() {
		return finishDescription;
	}

	public void setFinishDescription(String finishDescription) {
		this.finishDescription = finishDescription;
	}
}
