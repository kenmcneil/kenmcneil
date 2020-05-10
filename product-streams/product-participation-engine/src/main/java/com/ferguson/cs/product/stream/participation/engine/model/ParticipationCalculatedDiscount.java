package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a row in the participationCalculatedDiscount table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationCalculatedDiscount implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer participationId;
	private Integer pricebookId;
	private Double changeValue;
	private Boolean isPercent;
	private Integer templateId;
}
