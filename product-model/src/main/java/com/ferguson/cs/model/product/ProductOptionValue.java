package com.ferguson.cs.model.product;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A list of pre-defined values can associated with a product option and the user may only select one of those pre-defined values
 * when adding the product to a cart/project. The value defined here must conform to the parent's attribute definition's validation
 * rules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionValue implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique ID of the product option value.
	 */
	@Id
	private Integer id;
	private String value;
	/**
	 * Selecting this value may impact the base price of the product. This is the dollar amount that will be applied to the product's cost.
	 * TBD: We may need something more sophisticated (or in addition to a base price) when we need to consider price books.
	 */
	private BigDecimal price;
}
