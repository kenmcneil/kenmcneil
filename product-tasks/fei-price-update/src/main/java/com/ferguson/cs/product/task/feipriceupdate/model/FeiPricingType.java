package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum FeiPricingType implements IntMappedEnum, StringMappedEnum {
	PERMANENT(1,"Permanent"),PROMO(2,"Promo");

	private int intValue;
	private String stringValue;

	FeiPricingType(int intValue, String stringValue) {
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
