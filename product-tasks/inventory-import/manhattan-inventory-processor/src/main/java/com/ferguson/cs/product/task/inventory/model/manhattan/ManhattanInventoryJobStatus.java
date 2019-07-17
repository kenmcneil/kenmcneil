package com.ferguson.cs.product.task.inventory.model.manhattan;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum ManhattanInventoryJobStatus implements IntMappedEnum {
	LOADING(1),
	PROCESSING(2),
	COMPLETE(3),
	FAILED(4);

	private int intValue;

	ManhattanInventoryJobStatus(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
