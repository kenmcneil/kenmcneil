package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum ProductSyncStatus  implements IntMappedEnum, StringMappedEnum {
	ENTERED(1, "Upload Job Entered"),
	LOADING(2, "Loading Project data"),
	REVIEW(3, "Data Team Review"),
	PUBLISHING(4, "Publishing"),
	PUBLISHED(5, "Published"),
	PARTIAL(6, "Partial"),
	COMPLETE(7, "Complete"),
	CANCELLED(8, "Cancelled"),
	ERROR_VALIDATION(9, "Error: Validation"),
	ERROR_UPLOAD(10, "Error: Upload"),
	ERROR(11, "General Error");

	private final int intValue;
	private final String stringValue;

	ProductSyncStatus(int intValue, String stringValue) {
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
