package com.ferguson.cs.product.stream.participation.engine.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationCalculatedDiscount {
	private Integer id;
	private Integer pricebookId;
	private Double changeValue;
	private Boolean isPercent;
	private Integer templateId;
}
