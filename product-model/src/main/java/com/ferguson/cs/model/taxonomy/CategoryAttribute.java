package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.ferguson.cs.model.attribute.AttributeDefinitionReference;

import lombok.Builder;
import lombok.Value;

/**
 * A category within a taxonomy's hierarchy can have one or more attributes associated with it. These attributes represent characteristics
 * common to products thats belong (or will be added) to the category. The category attributes can be used to derive search facets and
 * enforce validation rules when products are added to the category (or its sub-categories).
 *
 * @author tyler.vangorder
 */
@Value
@Builder
public class CategoryAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	/**
	 * Link to the attribute definition which defines validation rules for this attribute.
	 */
	private AttributeDefinitionReference definition;

	/**
	 * This flag can be used to mark an attribute such that it is not visible to the end user.
	 */
	private boolean hidden;

	/**
	 * This flag indicates that any product added to the category MUST have the an attribute with the same definition.
	 */
	private boolean required;
}
