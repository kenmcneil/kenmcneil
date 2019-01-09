package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A category is a grouping of products and sub-categories within a taxonomy's classification system. The categories are organized into a
 * hierarchy of parent/children categories. Each category also defines a list of traits to better identify the types of products that can be placed
 * into that category. A categories traits can be marked as "required" or "optional" and a product can only be added to the category if it
 * has all of the required traits defined in the category. 
 *
 * Each trait within the category is associated with an attribute definition {@link com.ferguson.cs.model.attribute.AttributeDefinition}.  These
 * same definitions are used when defining product attributes and that is how traits are mapped to a product's attribute.
 * 
 * The category traits are also intended to be used to derive search facets and the attribute definitions define the datatype and validation rules.
 *  
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private Long taxonomyCode;
	private Long categoryIdParent;
	
	private String code;
	private String name;
	private String description;	
	private String parentCategoryId;
	private List<Category> childCategoryList;
	private List<CategoryTrait> traitList;
}
