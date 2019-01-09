package com.ferguson.cs.model.taxonomy;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String id;
	Long taxonomyCode;
	Long categoryIdParent;
	
	String code;
	String name;
	String description;	
	String parentCategoryId;
	List<Category> childCategoryList;

	List<CategoryAttribute> attributeList;
}
