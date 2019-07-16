package com.ferguson.cs.product.task.inventory.model.manhattan;

import com.ferguson.cs.utilities.IntMappedEnum;
import com.ferguson.cs.utilities.StringMappedEnum;

public enum ManhattanChannel implements IntMappedEnum, StringMappedEnum {
	BUILD(1, "build"), SUPPLY(2, "supply"), HMWALLACE(3, "hmwallace");

	final int intValue;
	final String stringValue;

	ManhattanChannel(int intValue, String stringValue) {
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
