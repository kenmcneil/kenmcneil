package com.ferguson.cs.model.brand;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A business unit is an organization entity within Ferguson which might have different operational and financial rules.
 * 
 * A business unit has one or more "channels" in which products are sold. Each channel may have different product
 * catalogs, taxonomies, pricing, and vendors.
 * 
 * It is also important to note that different channels may require the product data to be mutated from the "master"
 * data to apply branding, follow specific marketplace rules, or to implement a specific SEO strategy.
 */
@Getter @Setter @NoArgsConstructor @ToString
public class BusinessUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The unique ID of the business unit
	 */
	private String id;
		
	/**
	 * Name of the business unit
	 */
	private String name;

	/**
	 * The list of channels in which products are sold through this business unit.
	 */
	List<Channel> channelList;
}
