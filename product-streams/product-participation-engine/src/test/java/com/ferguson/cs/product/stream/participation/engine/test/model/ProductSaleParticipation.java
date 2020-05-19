package com.ferguson.cs.product.stream.participation.engine.test.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a record in the mmc.product.sale table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaleParticipation {
	private Integer uniqueId;
	private Integer saleId;
	private Integer participationId;
}
