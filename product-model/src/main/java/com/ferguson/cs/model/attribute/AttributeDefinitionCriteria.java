package com.ferguson.cs.model.attribute;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttributeDefinitionCriteria {
	/**
	 * Unique persistent ID assigned to the attribute definition.
	 */
	@Id
	private Integer attributeDefinitionId;

	/**
	 * A unique business key assigned to the attribute definition.
	 *
	 * This value is required.
	 */
	private String attributeDefinitionCode;

	/**
	 * Search by the attribute's description, this field may contain wildcards.
	 *
	 * This value is required.
	 */
	private String attributeDefinitionDescription;

	/**
	 * Search for attribute definitions with a given unit of measure.
	 */
	private String unitOfMeasureCode;

}
