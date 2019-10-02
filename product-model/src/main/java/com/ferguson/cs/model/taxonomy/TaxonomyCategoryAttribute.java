package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A category within a taxonomy's hierarchy can have one or more attributes associated with it. These attributes represent characteristics
 * common to products thats belong (or will be added) to the category. The category attributes can be used to derive search facets and
 * enforce validation rules when products are added to the category (or its sub-categories).
 *
 * @author tyler.vangorder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyCategoryAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	/**
	 * Link to the attribute definition which defines validation rules for this attribute.
	 */
	private AttributeDefinitionReference definition;

	/**
	 * This flag indicates that any product added to the category MUST have the an attribute with the same definition.
	 */
	private boolean required;

	public static class TaxonomyCategoryAttributeBuilder {

		public TaxonomyCategoryAttributeBuilder definition(AttributeDefinitionReference definition) {
			this.definition = definition;
			return this;
		}

		public TaxonomyCategoryAttributeBuilder definition(AttributeDefinition definition) {
			this.definition = new AttributeDefinitionReference(definition);
			return this;
		}
	}
}
