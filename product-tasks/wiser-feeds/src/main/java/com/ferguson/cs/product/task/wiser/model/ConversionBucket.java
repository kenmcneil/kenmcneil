package com.ferguson.cs.product.task.wiser.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum ConversionBucket implements IntMappedEnum, StringMappedEnum {
	LOW(1,"L"),MEDIUM(2,"M"),HIGH(3,"H");

	private int intValue;
	private String stringValue;

	ConversionBucket(int intValue, String stringValue) {
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
