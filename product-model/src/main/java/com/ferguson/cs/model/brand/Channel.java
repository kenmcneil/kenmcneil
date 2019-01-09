package com.ferguson.cs.model.brand;

import java.io.Serializable;

import com.ferguson.cs.model.taxonomy.Taxonomy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A channel represents a distribution channel through which products are sold. 
 * 
 *  A business unit may have one or more channels may have different product catalogs, taxonomies, pricing, and vendors.
 * 
 * It is also important to note that different channels may require the product data to be mutated from the "master"
 * data to apply branding, follow specific marketplace rules, or to implement a specific SEO strategy.
 * <P>
 * A channel can be associated with one or more taxonomy structures that are used to organize/classify products into a
 * hierarchy of categories/sub-categories. Currently this model provides the ability to map two taxonomy structures for each
 * channel: 
 * <P>
 * <table border="1">
 * <tr>
 * 		<td>primary</td>
 * 		<td>The primary product classification hierarchy that only allows a product to be placed exactly once within the hierarchy.
 * 				This taxonomy is appropriate for use when deriving SEO searches or for mirroring the classification structure defined by
 * 				a third-party marketplace.</td>
 * </tr>
 * <tr>
 * 		<td>navigation</td>
 * 		<td>Product classification hierarchy that mirrors a channel's navigation system. Products can be listed in more than on place within the hierarchy</td>
 * </tr>
 * </table>
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique ID of the channel.
	 */
	private String id;
	
	/**
	 * A short description of the channel.
	 */
	private String description;
	
	/**
	 * The type of distribution channel for products and associated information.
	 */
	private ChannelType channelType;
	
	/**
	 * The business unit associated with this channel.
	 */
	private BusinessUnit businessUnit;
	
	/**
	 * Are products being actively sold on this channel?
	 */
	private Boolean isActive;
	
	/**
	 * The primary product classification hierarchy that only allows a product to be placed exactly once within the hierarchy. This taxonomy is appropriate for use when deriving SEO searches or for
	 * mirroring the classification structure defined by a third-party marketplace.  
	 */
	private Taxonomy primaryTaxonomy;
	
	/**
	 * Product classification hierarchy that mirrors a channel's navigation system. Products can be listed in more than on place within the hierarchy
	 */
	private Taxonomy navigationTaxonomy;

}
