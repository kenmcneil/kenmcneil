package com.ferguson.cs.product.task.inventory.model;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum InventoryImportJobStatus implements IntMappedEnum {
	COMPLETE(1),
	IN_PROGRESS(2),
	FAILED(3),
	PARTIAL_FAILURE(4);

	private final int intValue;

	InventoryImportJobStatus(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
