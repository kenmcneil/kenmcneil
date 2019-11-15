package com.ferguson.cs.model.product;

import java.io.Serializable;

import org.springframework.data.repository.query.Param;

import lombok.Value;

/**
 * A variant identifier type represents an an enumeration of the types of identifiers that can be used to link to a specific product variant.
 *
 * The list of identifier types is stored in a backing database but there is not direct API to manipulate the values in this table.
 *
 * Examples of identifier types are : UPC, SKU, MPN, etc.
 *
 * @author tyler.vangorder
 */
@Value
public class ProductVariantIdentifierTypeReference implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * A unique business key used to identify the type.
	 */
	private String code;

	/**
	 * A description of the identifier type.
	 */
	private String description;

	public ProductVariantIdentifierTypeReference(@Param("code") String code, @Param("description") String description) {
		super();
		this.code = code;
		this.description = description;
	}


}
