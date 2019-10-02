package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A sparse category reference that can be used to uniquely identify a category.
 *
 * @author tyler.vangorder
 */

@ToString
@Setter
@Getter
public class TaxonomyReference implements Serializable {
	private static final long serialVersionUID = 1L;

	public TaxonomyReference() {
	}

	public TaxonomyReference(Taxonomy taxonomy) {
		this.id = taxonomy.getId();
		this.code = taxonomy.getCode();
		this.description = taxonomy.getDescription();
	}
	private Integer id;

	/**
	 * Unique business identifier assigned to the taxonomy.
	 */
	private String code;

	/**
	 * Description of the taxonomy
	 */
	private String description;

}
