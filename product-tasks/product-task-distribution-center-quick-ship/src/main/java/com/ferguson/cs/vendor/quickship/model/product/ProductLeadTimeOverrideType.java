package com.ferguson.cs.vendor.quickship.model.product;

import com.ferguson.cs.utilities.IntMappedEnum;

/**
 * Enumeration representing the various product lead time override types.
 *
 * @author francisco.cha
 */
public enum ProductLeadTimeOverrideType implements IntMappedEnum {

	IN_STOCK(1, "In Stock"),
	OUT_OF_STOCK(2, "Out of Stock"),
	MADE_TO_ORDER(3, "Made to Order"),
	PRE_ORDER(4, "Pre-Order");

	private final int intValue;
	private final String stringValue;

	ProductLeadTimeOverrideType(int intValue, String stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}

}
