package com.ferguson.cs.model.brand;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A channel represents a distribution channel through which products are sold. 
 * 
 *  A business unit may have one or more channels may have different product catalogs, taxonomies, pricing, and vendors.
 * 
 * It is also important to note that different channels may require the product data to be mutated from the "master"
 * data to apply branding, follow specific marketplace rules, or to implement a specific SEO strategy.
 * 
 * @author tyler.vangorder
 */
@Getter @Setter @NoArgsConstructor
public class Channel {

	/**
	 * Unique ID of the channel.
	 */
	String id;
	
	/**
	 * A short description of the channel.
	 */
	String description;
	
	/**
	 * The type of distribution channel for products and associated information.
	 */
	ChannelType channelType;
	
	/**
	 * The business unit associated with this channel.
	 */
	BusinessUnit businessUnit;
	
	/**
	 * Are products being actively sold on this channel?
	 */
	Boolean isActive;
}
