package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

/**
 * Enumeration representing the various product statuses.
 *
 * @author francisco.cha
 */
public enum ProductStatus implements IntMappedEnum, StringMappedEnum {

	DISCONTINUED(1, "discontinued"),
	NONSTOCK(2, "nonstock"),
	NOT_APPROVED(3, "not_approved"),
	PENDING(4, "pending"),
	REMOVED(5, "removed"),
	STOCK(6, "stock"),
	TEMPORARY_REMOVAL(7, "temporary_removal");

	private final int intValue;
	private final String stringValue;

	ProductStatus(int intValue, String stringValue) {
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
