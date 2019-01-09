package com.ferguson.cs.model.attribute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Base attribute definition for all numeric types that allows min/max rules to be defined on those numeric values. Additionally, a numeric value
 * may require a unit of measure as a qualifier when the value is used to represent something like length, weight, etc. If a product requires length
 * represented with two different units of measure, two attributes with difference units of measure must be used. 
 * 
 * @author tyler.vangorder
 *
 * @param <T>
 */
@Getter @Setter @NoArgsConstructor @ToString(callSuper=true)
public class NumericAttributeDefinition<T> extends AttributeDefinition<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * An optional unit of measure to better qualify the numeric value. Examples of unit of measure are : inches, centimeters, pounds, kilometers, etc
	 */
	private UnitOfMeasure unitOfMeasure;	

	/**
	 * An optional minimum value that can be assigned to the attribute's value.
	 */
	private T minimumValue;

	/**
	 * An optional maximum value that can be assigned to the attribute's value.
	 */
	private T maximumValue;

}
