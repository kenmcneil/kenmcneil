package com.ferguson.cs.product.stream.participation.engine.model;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum ParticipationCalculatedDiscountTemplateType implements IntMappedEnum {
	PERCENT(1),
	AMOUNT(2);

	private final int id;

	ParticipationCalculatedDiscountTemplateType(int id) {
		this.id = id;
	}

	@Override
	public int getIntValue() {
		return id;
	}

}
