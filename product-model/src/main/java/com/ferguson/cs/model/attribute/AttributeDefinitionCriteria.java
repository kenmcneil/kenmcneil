package com.ferguson.cs.model.attribute;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.ferguson.cs.utilities.ArgumentAssert;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttributeDefinitionCriteria {
	/**
	 * Unique persistent ID assigned to the attribute definition.
	 */
	@Id
	private Set<Integer> attributeDefinitionIds;

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

	public static class AttributeDefinitionCriteriaBuilder {

		public AttributeDefinitionCriteriaBuilder attributeDefinitionId(Integer id) {
			ArgumentAssert.notNull(id, "id");
			if (attributeDefinitionIds == null) {
				attributeDefinitionIds = new HashSet<>();
			}
			attributeDefinitionIds.add(id);
			return this;
		}
	}

}
