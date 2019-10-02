package com.ferguson.cs.model.taxonomy;

import java.util.Set;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaxonomyCategoryCriteria {

	/**
	 * The persistent ID of the taxonomy used to limit the category search to a specific taxonomy structure.
	 */
	private Integer taxonomyId;

	/**
	 * The unique business key of the taxonomy used to limit the category search to a specific taxonomy structure.
	 */
	private String taxonomyCode;

	/**
	 * The persistent ID of a "parent" taxonomy category used to limit the category search to just the immediate children of the parent.
	 */
	private Long categoryIdParent;

	/**
	 * A list of persistent taxonomy category IDs that should be retrieved.
	 */
	private Set<Long> categoryIds;

	/**
	 * A list of persistent taxonomy category IDs that should be retrieved.
	 */
	private Set<String> categoryCodes;

	/**
	 * The category path assigned to a category
	 */
	private String categoryPath;

	/**
	 * An optional qualifier, typically used in combination with the categoryIdParent to find a specific child by name.
	 */
	private String categoryName;

}
