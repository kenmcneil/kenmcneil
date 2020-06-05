package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;

public class MdmProductView implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Double score;
	private MdmProductAttributesView attributes;

	private MdmVendorView primaryVendor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public MdmProductAttributesView getAttributes() {
		return attributes;
	}

	public void setAttributes(MdmProductAttributesView attributes) {
		this.attributes = attributes;
	}

	public MdmVendorView getPrimaryVendor() {
		return primaryVendor;
	}

	public void setPrimaryVendor(MdmVendorView primaryVendor) {
		this.primaryVendor = primaryVendor;
	}

}
