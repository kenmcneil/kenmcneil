package com.ferguson.cs.model.attribute;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum AttributeDatatype implements IntMappedEnum {
	BOOLEAN(1),
	NUMERIC(2),
	STRING(3);

	private int id;

	private AttributeDatatype(int id) {
		this.id = id;
	}
	@Override
	public int getIntValue() {
		return id;
	}
}
