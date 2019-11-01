package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;

import com.ferguson.cs.utilities.ArgumentAssert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A category reference
 *
 * @author tyler.vangorder
 */
@ToString
@Setter
@Getter
public class TaxonomyCategoryReference implements Serializable {
	private static final long serialVersionUID = 1L;

	public TaxonomyCategoryReference() {
	}

	public TaxonomyCategoryReference(TaxonomyCategory category) {
		ArgumentAssert.notNull(category, "category");
		this.id = category.getId();
		this.taxonomy = category.getTaxonomy();
		this.path = category.getPath();
		this.code = category.getCode();
		this.name = category.getName();
		this.description = category.getDescription();
	}

	/**
	 * Unique persistence ID assigned to the category
	 */
	private  Long id;

	/**
	 * The taxonomy under which this category exists.
	 */
	private TaxonomyReference taxonomy;

	private String path;

	/**
	 * A business key assigned to this category that is unique within the context of the taxonomy but is not globally unique.
	 */
	private String code;

	/**
	 * The name of the category ("Lights", "Bathroom", etc)
	 */
	private String name;

	/**
	 * Description of the category
	 */
	private String description;

}
