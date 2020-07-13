package com.ferguson.cs.product.task.feipricefeed.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum FeiPriceDataStatus implements IntMappedEnum, StringMappedEnum {
	VALID(1,"Valid"),
	UNRESOLVED_DUPLICATE_MPID(2,"Unresolved duplicate MPID"),
	LOW_MARGIN(3, "Low margin"),
	OVERRIDE(4, "Override");

	private int intValue;
	private String stringValue;

	FeiPriceDataStatus(int intValue,String stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}


	@Override
	public int getIntValue() {
		return intValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
