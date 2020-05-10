package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricebookCost implements Serializable {
	private static final long serialVersionUID = 4L;

	private Integer uniqueId;
	private Integer pricebookId;
	private Double cost;
	private Float multiplier;
	private Double basePrice;
	private Integer userId;
	private Integer participationId;
}
