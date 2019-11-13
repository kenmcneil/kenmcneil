package com.ferguson.cs.model.product;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A product variant is a salable item that is linked to carts and orders. There are often multiple identifiers that can be used
 * to uniquely identify a product variant. A variant identifier is unique when combining the both the type and identifier.
 *
 * @author tyler.vangorder
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantIdentifier implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The variant identifier type code identifies the type of identifier and maps to the VariantIndentifyType code.
	 */
	private ProductVariantIdentifierTypeReference type;

	/**
	 * The variant's identifier value.
	 */
	private String identifierValue;
}
