package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum ContainerType implements IntMappedEnum, StringMappedEnum {
	BUNDLE(1,"Bundle"),
	PACKAGE(2,"Package"),
	PRODUCT(3,"product");

	private int intValue;
	private String stringValue;

	ContainerType(int intValue, String stringValue) {
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
