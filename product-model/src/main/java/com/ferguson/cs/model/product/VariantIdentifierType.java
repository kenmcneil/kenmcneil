package com.ferguson.cs.model.product;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A variant identifier type represents an an enumeration of the types of identifiers that can be used to link to a specific product variant.
 * 
 * The list of identifier types will likely be dynamic and change over time. The types will be stored in a persistent data store and can be 
 * references by the "code" which is a unique, business key.
 * 
 * Examples of identifier types are : UPC, SKU, MPN, etc. 
 * 
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class VariantIdentifierType implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * A unique business key used to identify the type.
	 */
	private String code;
	
	/**
	 * A description of the identifier type.
	 */
	private String description;

}
