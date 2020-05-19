package com.ferguson.cs.product.stream.participation.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a row in the mmc.product.participationCalculatedDiscount table.
 * The primary key [id] is intentionally omitted since it is never referenced,
 * and to avoid confusing it with the participationId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationCalculatedDiscount {
	private Integer participationId;
	private Integer pricebookId;
	private Double changeValue;
	private Boolean isPercent;
	private Integer templateId;
}
