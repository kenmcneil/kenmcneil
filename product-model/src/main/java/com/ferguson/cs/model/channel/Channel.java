package com.ferguson.cs.model.channel;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.ferguson.cs.model.taxonomy.Taxonomy;

import lombok.Builder;
import lombok.Data;

/**
 * A channel represents a distribution channel through which products are sold.
 *
 *  A business unit may have one or more channels that have different product catalogs, taxonomies, pricing, and vendors.
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
@Data
@Builder
public class Channel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique ID of the channel.
	 */
	private String id;

	/**
	 * Unique business key for the channel.
	 */
	@Indexed(unique=true)
	private String code;

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
	 * A product taxonomy is a hierarchical classification system where products are grouped into categories/sub-categories. A product category
	 * is a grouping of products and can, optionally, have a set of sub-categories that can be used to drill-down into more-specific groupings. A
	 * category also has a set of "traits" that define what types of products can be added to that category.
	 * <p>
	 *
	 * A channel allows more than one taxonomy to be assigned to it. This allows for one taxonomy that can be u
	 */
	@DBRef
	private List<Taxonomy> taxonomyList;


}
