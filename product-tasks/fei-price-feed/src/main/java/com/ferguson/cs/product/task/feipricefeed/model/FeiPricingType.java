package com.ferguson.cs.product.task.feipricefeed.model;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum FeiPricingType implements IntMappedEnum {
	PERMANENT(1),PROMO(2);

	private int intValue;

	FeiPricingType(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
