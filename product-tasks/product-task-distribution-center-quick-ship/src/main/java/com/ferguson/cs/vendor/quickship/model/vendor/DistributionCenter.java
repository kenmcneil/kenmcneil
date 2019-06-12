package com.ferguson.cs.vendor.quickship.model.vendor;

import java.io.Serializable;

/**
 * Vendor domain still needs to be refactored so this will be a placeholder object so that we can differentiate between
 * a vendor and a distribution center
 *
 * @author francisco.cha
 *
 */
public class DistributionCenter implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
