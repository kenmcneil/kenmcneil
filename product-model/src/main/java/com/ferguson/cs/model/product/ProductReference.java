package com.ferguson.cs.model.product;

import java.io.Serializable;

import lombok.Value;

/**
 * A sparse product reference that can be used to uniquely identify a product.
 *
 * @author tyler.vangorder
 */
@Value
public class ProductReference implements Serializable {
	private static final long serialVersionUID = 1L;
	Long id;

	public ProductReference(Product product) {
		this.id = product.getId();
	}
}
