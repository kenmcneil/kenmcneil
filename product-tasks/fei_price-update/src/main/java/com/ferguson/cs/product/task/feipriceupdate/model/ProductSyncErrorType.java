package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum ProductSyncErrorType implements IntMappedEnum, StringMappedEnum {
	UPLOAD(1, "UPLOAD"),
	VALIDATION(2, "VALIDATION"),
	PUBLISH(3, "PUBLISH");

	private final int intValue;
	private final String stringValue;

	ProductSyncErrorType(int intValue, String stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}


}
