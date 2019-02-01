package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Transient;

import lombok.Builder;
import lombok.Data;

/**
 * A category is a grouping of products and sub-categories within a taxonomy's classification system. The categories are organized into a
 * hierarchy of parent/children categories. Each category also defines a list of traits to better identify the types of products that can be placed
 * into that category. A categories traits can be marked as "required" or "optional" and a product can only be added to the category if it
 * has all of the required traits defined in the category.
 *
 * Each trait within the category is associated with an attribute definition {@link com.ferguson.cs.model.attribute.AttributeDefinition}.  These
 * same definitions are used when defining product attributes and that is how traits are mapped to a product's attribute.
 *
 * The category traits can also be used to derive search facets and the attribute definitions define the datatype and validation rules.
 *
 * @author tyler.vangorder
 */
@Data
@Builder
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistence ID assigned to the category
	 */
	private String id;

	/**
	 * The taxonomy code to which this category belongs.
	 */
	private Long taxonomyCode;

	/**
	 * A business key assigned to this category that is unique within the context of the taxonomy but is not globally unique.
	 */
	private String code;

	/**
	 * The name of the category ("Lights", "Bathroom", etc)
	 */
	private String name;

	/**
	 * Description of the category
	 */
	private String description;

	/**
	 * The persistent ID of the parent category, top-level categories will NOT have a parent.
	 */
	private String categoryIdParent;

	/**
	 * A list of traits that are common for products that are assigned to this category.
	 */
	private List<CategoryTrait> traitList;

	/**
	 * A list of subcategories belonging to this category.
	 */
	@Transient
	private List<Category> subcategoryList;

	/**
	 * A list of product IDs that have been assigned to this category. A product can only be assigned to this category if
	 * it has all of the category's required traits.
	 */
	private List<String> productIdList;
}
