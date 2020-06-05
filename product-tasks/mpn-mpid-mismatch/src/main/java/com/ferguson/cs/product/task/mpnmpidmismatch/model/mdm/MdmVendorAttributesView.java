package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdmVendorAttributesView implements Serializable {

	private static final long serialVersionUID = 1L;

	private String address1;
	private String address2;
	private String zipCode;
	private String phoneNumber;
}
