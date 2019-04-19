package com.ferguson.cs.product.task.inventory.model.manhattan;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum ManhattanIntakeJobStatus implements IntMappedEnum {
	LOADING(1),
	READY_FOR_PROCESSING(2),
	COMPLETE(3),
	FAILED(4);

	private int intValue;
	ManhattanIntakeJobStatus(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
