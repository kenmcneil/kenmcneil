package com.ferguson.cs.product.stream.participation.engine.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a Was price value to put in the mmc.dbo.pricebookWasPrice table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasPriceFixture {
	private Integer uniqueId;
	private Double wasPrice;
}
