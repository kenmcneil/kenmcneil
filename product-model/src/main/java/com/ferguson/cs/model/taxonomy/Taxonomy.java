package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A taxonomy defines a product classification hierarchy organized as a set of category trees. A category can have a list of child categories and
 * contains a set of products. The category also defines a set of "traits" for which the products included in that category. Some of those traits
 * may be mandatory, while others are optional. The attribute definitions associated with the trait are the same definitions used when defining
 * product attributes. A product cannot be added to a category if it does not have all of the required traits.
 * 
 * Q 
 *  
 * 
 * @author tyler.vangorder
 *
 */
@Getter @Setter @NoArgsConstructor @ToString
public class Taxonomy implements Serializable {

	private static final long serialVersionUID = 1L;

	String id;
	String code;
	String description;
	
	List<Category> rootCategoryList;
}
