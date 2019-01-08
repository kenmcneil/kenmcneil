package com.ferguson.cs.model.taxonomy;

import java.util.List;

public class TaxonomyCategory {
	
	Long id;
	Long taxonomyCode;	

	String code;

	String name;
	String description;	
	TaxonomyCategory parentCategory;
	List<TaxonomyCategory> childCategoryList;

	List<TaxonomyAttribute> attributeList;
}
