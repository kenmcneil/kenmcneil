package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;

import com.ferguson.cs.model.attribute.AttributeDefinition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A category within a taxonomy's hierarchy can have one or more attributes associated with it. These attributes represent characteristics
 * common to products thats belong (or will be added to the category). The category attributes can be used to derive search facets and
 * enforce validation rules when products are added to the category (or its sub-categories). 
 *  
 * @author tyler.vangorder
 */
@Getter @Setter @NoArgsConstructor @ToString
public class CategoryAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	
	/**
	 * Link to the attribute definition which defines validation rules for this attribute.
	 */
	private AttributeDefinition<?> definition;
	
	/**
	 * This flag indicates that any product added to the category MUST have the an attribute with the same definition.
	 */
	boolean required;
	
	/**
	 * This flag can be used to determine if the attribute is visible within the store front (or available as a search facet).
	 */
	boolean hidden;
}
