package com.ferguson.cs.model.product;

import java.io.Serializable;

import com.ferguson.cs.model.attribute.AttributeDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ProductAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Each product attribute must have a link to an attribute definition. The definition sets the datatype and the validation
	 * rules that should be applied to the attribute value.
	 */
	private AttributeDefinition definition;

	/**
	 * This flag can be used to mark an attribute such that it is not visible to the end user.
	 */
	private boolean hidden;

	/**
	 * Can this attribute be overridden at the variant level? Setting this value to false precludes the variants from overriding the value defined at the product level.
	 */
	private boolean overrideAllowed;

	/**
	 * This value of this attribute must conform to the rules defined by attribute definition linked to this attribute:
	 * <p>
	 * <li>This value must be able to be parsed into the datatype defined for the attribute definition.</li>
	 * <li>If there are enumerated values in the definition, this value MUST be equal to one of those value (or null)</li>
	 * <li>If min/max values are defined on the attribute definition (on numeric data types), this value must be within the range (or null)<li>
	 */
	private String value;

}
