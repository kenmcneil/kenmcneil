package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdmProductView implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Double score;
	private MdmProductAttributesView attributes;
	private MdmVendorView primaryVendor;
}
