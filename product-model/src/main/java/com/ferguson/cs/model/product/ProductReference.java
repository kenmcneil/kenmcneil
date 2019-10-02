package com.ferguson.cs.model.product;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A sparse product reference that can be used to uniquely identify a product.
 *
 * @author tyler.vangorder
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReference implements Serializable {
	private static final long serialVersionUID = 1L;
	Long id;

	public ProductReference(Product product) {
		this.id = product.getId();
	}
}
