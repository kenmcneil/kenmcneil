package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

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

	@Id
	private Integer id;
	private Integer participationId;
	private Integer priceBookId;
	private Double changeValue;
	private boolean isPercent;
	private Integer templateId;

	public ParticipationCalculatedDiscount(
			Integer participationId,
			Integer priceBookId,
			Double changeValue,
			boolean isPercent,
			Integer templateId
	) {
		this.participationId = participationId;
		this.priceBookId = priceBookId;
		this.changeValue = changeValue;
		this.isPercent = isPercent;
		this.templateId = templateId;
	}
}
