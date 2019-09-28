package com.ferguson.cs.model;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IdCodeCriteria {

	/**
	 * Unique persistence ID.
	 */
	@Id
	private Integer id;

	/**
	 * Unique business key.
	 */
	private String code;

}
