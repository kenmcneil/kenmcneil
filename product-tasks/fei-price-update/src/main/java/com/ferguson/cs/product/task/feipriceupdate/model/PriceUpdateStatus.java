package com.ferguson.cs.product.task.feipriceupdate.model;

import com.ferguson.cs.utilities.IntMappedEnum;

public enum PriceUpdateStatus implements IntMappedEnum  {
	VALID(0),
	LOW_MARGIN_ERROR(1),
	DATA_MATCH_ERROR(2),
	OWNED_INACTIVE_ERROR(3),
	OWNED_LOOKUP_ERROR(4),
	PRICE_VALIDATION_ERROR(5),
	INPUT_VALIDATION_ERROR(6),
	VENDOR_COST_LOOKUP_ERROR(7);

	final int intValue;

	PriceUpdateStatus(int intValue) {
		this.intValue = intValue;
	}

	@Override
	public int getIntValue() {
		return intValue;
	}
}
