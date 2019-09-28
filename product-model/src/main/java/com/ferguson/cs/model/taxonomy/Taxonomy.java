package com.ferguson.cs.model.taxonomy;

import java.time.LocalDateTime;

import com.ferguson.cs.model.Auditable;

import lombok.Builder;
import lombok.Data;

/**
 * A taxonomy defines a product classification hierarchy organized as a set of category trees. A category can have a list of child categories and
 * contains a set of products. The category also defines a set of "traits" to indicate what types of products can be included within the category.
 * Some of the traits may be mandatory, while others are optional. A product should not be added to a category if it does not have all of the required
 * traits of that category.
 *<p>
 *  A taxonomy can be marked as "strict" which means that a product may only be assigned ONCE within the classification hierarchy. A "non-strict"
 *  taxonomy allows a product to be placed within multiple categories within the classification hierarchy. A "strict" taxonomy is more appropriate
 *  for defining SEO strategies while a "non-strict" taxonomy can be used for site navigation.  Each channel is associated with a taxonomy and more
 *  than one channel can reuse the same taxonomy structure.
 *<p>
 *  <b>EXAMPLE:</b> A non-strict taxonomy can be used to mirror the navigation system of a site where a ceiling fan (with a light) can be placed in both the "fan"
 *  category and a "light" category. The same product can also be added to the a strict taxonomy where it is ONLY placed in the "fan" category.
 *  <p>
 *
 * @author tyler.vangorder
 */
@Data
@Builder
public class Taxonomy implements Auditable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique database identifier assigned to the taxonomy.
	 */
	private String id;

	/**
	 * Unique business identifier assigned to the taxonomy.
	 */
	private String code;

	/**
	 * Description of the taxonomy
	 */
	private String description;

	/**
	 * A flag to indicate if a product can be placed more than once within the classification system. Setting this flag to "true"
	 * will only allow a product to be placed ONCE within the classification system.
	 */
	private Boolean isStrict;

	/**
	 * The root category is a special, top-level category under which the entire taxonomy structure is defined.
	 */
	private CategoryReference rootCategory;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;
	private Long version;

}
