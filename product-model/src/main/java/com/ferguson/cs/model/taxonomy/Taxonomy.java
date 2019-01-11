package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A taxonomy defines a product classification hierarchy organized as a set of category trees. A category can have a list of child categories and
 * contains a set of products. The category also defines a set of "traits" to indicate what types of products can be included within the category.
 * Some of the traits may be mandatory, while others are optional. A product cannot be added to a category if it does not have all of the required
 * traits of that category.
 * <p>
 * TODO: We may need to enforce that a product must have all required traits of its associated category, plus the required attributes of all
 * 				parent categories. TBD
 *<p>
 *  There is a top-level attribute on the taxonomy to indicate if taxonomy is "strict" or allows for a product to be placed in multiple categories. A
 *  "strict" taxonomy structure requires that a product can only be associated once within the classification system and is more appropriate when
 *  deriving concepts such as SEO strategy.
 *<p>
 *  <b>NOTE:</b> A non-strict taxonomy can be used to mirror the navigation system of a site where a ceiling fan (with a light) can be placed in both the "fan"
 *  category and a "light" category.
 *  <p>
 *  <b>NOTE:</b> A channel can have more than one taxonomy associated with it which allows one taxonomy to be used for visual layout and another for
 *  			defining the SEO strategy.
 *
 * @author tyler.vangorder
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class Taxonomy implements Serializable {

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
	private boolean strict;

	/**
	 * A set of "root" categories that represent the top-level categories within the classification system.
	 */
	private List<Category> rootCategoryList;
}
