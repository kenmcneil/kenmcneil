package com.ferguson.cs.model.attribute;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * An attribute provides a way to describe a characteristic about a product, a product variant, product customization, or trait within a
 * specific category. The attribute definition defines the "rules" that apply when assigning a value to an attribute and those rules
 * can be applied consistently regardless of where that attribute is linked.
 *   
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class AttributeDefinition <T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Unique persistent ID assigned to the attribute definition.
	 */
	private String id;
	
	/**
	 * A unique business key assigned to the attribute definition.
	 */
	private String code;
	
	/**
	 * A description of the attribute definition.
	 */
	private String description;
	
	/**
	 * A default value for an attribute.
	 */
	private T defaultValue;
	
	/**
	 * If an enumerated value list is defined on an attribute, the values in the list are the ONLY values that can be assigned to an attribute.
	 * 
	 *  EXAMPLE, if you define a string attribute definition to represent "shirt color", you can define a finite list of colors of that shirt: "red", "blue", "orange", etc. 
	 */
	private List<T> enumeratedValueList;

}
