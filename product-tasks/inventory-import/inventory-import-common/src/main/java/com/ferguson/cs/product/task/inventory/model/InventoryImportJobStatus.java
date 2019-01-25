package com.ferguson.cs.product.task.inventory.model;

public enum InventoryImportJobStatus implements IntMappedEnum {
	COMPLETE(1),
	IN_PROGRESS(2),
	FAILED(3);

	private final int intValue;

	InventoryImportJobStatus(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
