package com.ferguson.cs.product.stream.participation.engine.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationCalculatedDiscountTemplate implements Serializable {
	private static final long serialVersionUID = 2L;

	private Integer id;
	private ParticipationCalculatedDiscountTemplateType templateType;
	private String template;
	private boolean active;
	private String description;
}
