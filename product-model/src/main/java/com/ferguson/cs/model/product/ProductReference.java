package com.ferguson.cs.model.product;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;

/**
 * A sparse product reference that can be used to uniquely identify a product.
 *
 * @author tyler.vangorder
 */
@Value
@Builder
public class ProductReference implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
}
