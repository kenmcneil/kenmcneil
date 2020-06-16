package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManufacturerMasterVendor implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer manufacturerId;
	private Integer masterVendorId;
	private String masterVendorName;
}
