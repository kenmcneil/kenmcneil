package com.ferguson.cs.model.asset;

import com.ferguson.cs.utilities.IntMappedEnum;

/**
 * Used to denote the type of a digital resource. This is a work in progress and make require more specific types ("JPG, PDF, etc")
 * @author tyler.vangorder
 *
 */
public enum DigitalResourceType implements IntMappedEnum {

	IMAGE(1),
	DOCUMENT(2),
	AUGMENTED_REALITY_MODEL(3);

	private int id;

	private DigitalResourceType(int id) {
		this.id = id;
	}
	@Override
	public int getIntValue() {
		return id;
	}
}
