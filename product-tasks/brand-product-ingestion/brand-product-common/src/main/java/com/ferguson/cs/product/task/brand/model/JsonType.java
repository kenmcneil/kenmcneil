package com.ferguson.cs.product.task.brand.model;


public enum JsonType implements IntMappedEnum { 
	
	FILTER(1),
	INVENTORY(2),
	CATEGORY(3),
	FINISH(4),
	BRAND(5),
	IMAGE(6),
	FEATURE(7),
	ATTRIBUTE(8),
	ACCESSORY(9),
	RESTRICTION(10),
	DIMENSION(11),
	CUSTOM_PROPERTY(12);
	
	private final int  intValue;

	JsonType(int v) {
		intValue = v;
    }

    
	@Override
	public int getIntValue() {
		return intValue;
	}

    
}
