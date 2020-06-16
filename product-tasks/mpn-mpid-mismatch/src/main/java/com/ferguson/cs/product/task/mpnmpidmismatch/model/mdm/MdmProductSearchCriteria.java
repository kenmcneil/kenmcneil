package com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdmProductSearchCriteria implements Serializable {

	private static final long serialVersionUID = 3L;

	private MdmProductSearch mdmProductSearch;
	private List<ManufacturerMasterVendor> alternateMasterVendors;
	private Integer limit;
	private Operator operator;

	public enum Operator {
		AND,
		OR
	}
}
