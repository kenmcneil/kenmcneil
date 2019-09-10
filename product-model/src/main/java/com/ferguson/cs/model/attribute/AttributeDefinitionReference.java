package com.ferguson.cs.model.attribute;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

/**
 * An attribute provides a way to describe a characteristic about a product, a product variant, product option, or trait within a
 * specific category. The attribute definition defines the "rules" that apply when assigning a value to an attribute and those rules
 * can be applied consistently regardless of where that attribute is linked.
 *
 * @author tyler.vangorder
 */
@Data
@Builder
public class AttributeDefinitionReference implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique persistent ID assigned to the attribute definition.
	 */
	@Id
	private Integer id;

	/**
	 * A unique business key assigned to the attribute definition.
	 *
	 * This value is required.
	 */
	private String code;

	/**
	 * Indicates the datatype for the values of this attribute definition.
	 *
	 * This value is required.
	 */
	private AttributeDatatype datatype;

	/**
	 * A description of the attribute definition.
	 */
	private String description;

	/**
	 * An optional unit of measure to better qualify the numeric value. Examples of unit of measure are : inches, centimeters, pounds, kilometers, etc
	 */
	private UnitOfMeasureReference unitOfMeasure;

	/**
	 * An optional minimum value that can be assigned to the attribute's value.
	 * <p>
	 * This value must be able to be parsed into the datatype defined for the attribute definition.
	 */
	private String minimumValue;

	/**
	 * An optional maximum value that can be assigned to the attribute's value.
	 * <p>
	 * This value must be able to be parsed into the datatype defined for the attribute definition.
	 */
	private String maximumValue;

	/**
	 * A default value for an attribute.
	 * <p>
	 * This value must be able to be parsed into the datatype defined for the attribute definition.
	 */
	private String defaultValue;

	/**
	 * If an enumerated value list is defined on an attribute, the values in the list are the ONLY values that can be assigned to an attribute.
	 * <p>
	 * The values in this list must be able to be parsed into the datatype defined for the attribute definition.
	 * <p>
	 *  EXAMPLE, if you define a string attribute definition to represent "shirt color", you can define a finite list of colors of that shirt: "red", "blue", "orange", etc.
	 */
	private List<String> enumeratedValueList;

}
