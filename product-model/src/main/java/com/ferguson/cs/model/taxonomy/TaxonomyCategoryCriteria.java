package com.ferguson.cs.model.taxonomy;

import java.util.HashSet;
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
	 * The category path assigned to a category, the path is unique within a given taxonomy.
	 */
	private String categoryPath;

	/**
	 * A list of persistent taxonomy category IDs that should be retrieved.
	 */
	private Set<Long> categoryIds;

	/**
	 * A list of persistent taxonomy category IDs that should be retrieved.
	 */
	private Set<String> categoryCodes;

	/**
	 * The persistent ID of a "parent" taxonomy category used to limit the category search to just the immediate children of the parent.
	 */
	private Long categoryIdParent;

	public static class TaxonomyCategoryCriteriaBuilder {

		public TaxonomyCategoryCriteriaBuilder categoryId(Long categoryId) {
			if (this.categoryIds == null) {
				this.categoryIds= new HashSet<>();
			}
			this.categoryIds.add(categoryId);
			return this;
		}

		public TaxonomyCategoryCriteriaBuilder categoryCode(String categoryCode) {
			if (this.categoryCodes == null) {
				this.categoryCodes= new HashSet<>();
			}
			this.categoryCodes.add(categoryCode);
			return this;
		}

	}

}
