package com.ferguson.cs.model.taxonomy;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Version;

import com.ferguson.cs.model.Auditable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A taxonomy defines a product classification hierarchy organized as a set of category trees. A category can have a list of child categories and
 * contains a set of products. The category also defines a set of "attributes" to indicate what types of products can be included within the category.
 * Some of the attributes may be mandatory, while others are optional. A product may not be added to a category if it does not have all of the required
 * attributes of that category.
 *<p>
 *  A product may only be assigned ONCE within the classification hierarchy.
 *
 * @author tyler.vangorder
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Taxonomy implements Auditable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique database identifier assigned to the taxonomy.
	 */
	private Integer id;

	/**
	 * Unique business identifier assigned to the taxonomy.
	 */
	private String code;

	/**
	 * Description of the taxonomy
	 */
	private String description;

	/**
	 * The root category is a special, top-level category under which the entire taxonomy structure is defined.
	 */
	private TaxonomyCategoryReference rootCategory;

	//Audit Columns
	private LocalDateTime createdTimestamp;
	private LocalDateTime lastModifiedTimestamp;

	@Version
	private Integer version;

}
