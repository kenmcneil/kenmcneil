package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum PricebookType implements IntMappedEnum{
	PB1(1),
	PB22(22);

	final int intValue;

	PricebookType(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}

}
