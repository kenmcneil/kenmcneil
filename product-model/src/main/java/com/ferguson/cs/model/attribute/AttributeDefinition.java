package com.ferguson.cs.model.attribute;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * An attribute provides a way to describe a characteristic about a product, a product variant, product customization, or within a
 * specific category. The attribute definition defines the "rules" that apply when assigning a value to an attribute and those rules
 * can be applied consistently regardless of where that attribute is linked.
 *   
 * @author tyler.vangorder
 *
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class AttributeDefinition <T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String code;
	private String description;
	
	private T defaultValue;
	private List<T> enumeratedValueList;

}
