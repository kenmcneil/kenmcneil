package com.ferguson.cs.product.stream.participation.engine.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a non-discounted pricebook_cost record.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffsalePriceFixture {
	private Integer uniqueId;
	private Integer pricebookId;
	private Double price;
}
