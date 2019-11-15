package com.ferguson.cs.model.product;

import java.io.Serializable;

import com.ferguson.cs.model.attribute.AttributeDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A variant attribute is a product characteristic that is defined at the variant level. The collection of variant attributes are
 * what make each variant "unique" within the product family.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Each product attribute must have a link to an attribute definition. The definition sets the datatype and the validation
	 * rules that should be applied to the attribute value.
	 */
	private AttributeDefinition definition;

	/**
	 * This value of this attribute must conform to the rules defined by attribute definition linked to this attribute:
	 * <p>
	 * <li>This value must be able to be parsed into the datatype defined for the attribute definition.</li>
	 * <li>If there are enumerated values in the definition, this value MUST be equal to one of those value (or null)</li>
	 * <li>If min/max values are defined on the attribute definition (on numeric data types), this value must be within the range (or null)<li>
	 */
	private String value;

}
