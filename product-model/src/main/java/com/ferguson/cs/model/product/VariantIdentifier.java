package com.ferguson.cs.model.product;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A product variant is a salable item that is linked to carts and orders. There are often multiple identifiers that can be used
 * to uniquely identify a product variant. A variant identifier is unique when combining the both the type and identifier.
 * 
 * @author tyler.vangorder
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class VariantIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The variant Id associated with this identifier.
	 */
	private String variantId;
	
	/**
	 * The variant identifier type code identifies the type of identifier and maps to the VariantIndentifyType code.
	 */
	private String typeCode;

	/**
	 * The variant's identifier value.
	 */
	private String identifierValue;
}
