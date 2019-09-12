package com.ferguson.cs.product.task.wiser.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum RevenueCategory implements IntMappedEnum, StringMappedEnum {
	TAIL(1,"T"),CORE(2,"C"),HEAD(3,"H");

	private int intValue;
	private String stringValue;

	RevenueCategory(int intValue,String stringValue) {
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
