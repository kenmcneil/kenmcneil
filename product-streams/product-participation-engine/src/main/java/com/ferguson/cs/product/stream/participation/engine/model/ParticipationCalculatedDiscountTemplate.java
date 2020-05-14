package com.ferguson.cs.product.stream.participation.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This represents a row in the mmc.product.ParticipationCalculatedDiscountTemplate table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationCalculatedDiscountTemplate {
	private Integer id;
	private ParticipationCalculatedDiscountTemplateType templateType;
	private String template;
	private boolean active;
	private String description;
}
