package com.ferguson.cs.product.model;

public enum InventoryImportJobType implements IntMappedEnum{
	FTP(1),
	EMAIL(2);

	private final int intValue;

	InventoryImportJobType(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
