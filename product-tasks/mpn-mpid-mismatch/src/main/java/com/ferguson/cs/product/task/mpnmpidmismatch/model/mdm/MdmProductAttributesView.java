package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MdmProductAttributesView implements Serializable {

	private static final long serialVersionUID = 2L;

	private Long primaryVendorId;
	private String longDescription;
	private String freightCode;
	private String upc;
	private String vendorProductCode;	// SKU
	private String alternateCode1;
	private String unitOfMeasure;

}
