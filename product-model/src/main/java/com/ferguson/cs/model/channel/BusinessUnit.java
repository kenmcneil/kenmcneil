package com.ferguson.cs.model.channel;

import com.ferguson.cs.utilities.IntMappedEnum;

/**
 * A business unit is an organization entity within Ferguson which might have different operational and financial rules.
 *
 * A business unit has one or more "channels" in which products are sold. Each channel may have different product
 * catalogs, taxonomies, pricing, and vendors.
 *
 * It is also important to note that different channels may require the product data to be mutated from the "master"
 * data to apply branding, follow specific marketplace rules, or to implement a specific SEO strategy.
 */
public enum BusinessUnit  implements IntMappedEnum {

	BUILD(1, "Build"),
	SUPPLY(2, "Supply");

	private int id;
	private String name;

	private BusinessUnit(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int getIntValue() {
		return id;
	}

	/**
	 * Name of the business unit
	 */
	public String getName() {
		return name;
	}


}
