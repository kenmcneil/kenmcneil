package com.ferguson.cs.product.task.wiser.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum RecommendationJobFailureCause implements IntMappedEnum, StringMappedEnum {
	FILE_MISSING(1,"File was missing at cutoff"),
	INTERNAL(2, "Internal failure"),
	FTP(3, "Failed to access Wiser FTP server"),
	UNKNOWN_ERROR(4,"Unknown error");

	private int intValue;
	private String stringValue;

	RecommendationJobFailureCause(int intValue, String stringValue) {
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
