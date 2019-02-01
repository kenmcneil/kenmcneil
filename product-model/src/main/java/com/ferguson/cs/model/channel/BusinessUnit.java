package com.ferguson.cs.model.channel;

/**
 * A business unit is an organization entity within Ferguson which might have different operational and financial rules.
 *
 * A business unit has one or more "channels" in which products are sold. Each channel may have different product
 * catalogs, taxonomies, pricing, and vendors.
 *
 * It is also important to note that different channels may require the product data to be mutated from the "master"
 * data to apply branding, follow specific marketplace rules, or to implement a specific SEO strategy.
 */
public enum BusinessUnit  {

	BUILD("1", "Build"),
	SUPPLY("2", "Supply");

	private String id;
	private String name;

	private BusinessUnit(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * The unique ID of the business unit
	 */
	public String getId() {
		return id;
	}

	/**
	 * Name of the business unit
	 */
	public String getName() {
		return name;
	}


}
