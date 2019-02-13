package com.ferguson.cs.product.api.taxonomy;

import java.util.List;
import java.util.Optional;

import com.ferguson.cs.model.taxonomy.Category;
import com.ferguson.cs.model.taxonomy.CategoryReference;

public interface CategoryService {

	List<Category> getCategoriesByReferences(List<CategoryReference> categoryReferenceList);
	Optional<Category> getCategoryByReference(CategoryReference categoryReference);
	Optional<Category> getCategory(String code);
	Category saveCategory(Category category);
	void deleteCategory(Category category);

}
