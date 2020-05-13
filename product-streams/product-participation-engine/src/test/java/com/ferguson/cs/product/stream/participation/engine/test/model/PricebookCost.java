package com.ferguson.cs.product.stream.participation.engine.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a record in the mmc.dbo.Pricebook_Cost table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricebookCost {
	private Integer uniqueId;
	private Integer pricebookId;
	private Double cost;
	private Float multiplier;
	private Double basePrice;
	private Integer userId;
	private Integer participationId;
}
