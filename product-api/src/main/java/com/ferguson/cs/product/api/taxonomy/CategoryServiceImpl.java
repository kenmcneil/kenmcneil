package com.ferguson.cs.product.api.taxonomy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ferguson.cs.model.taxonomy.Category;
import com.ferguson.cs.model.taxonomy.CategoryReference;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class CategoryServiceImpl implements CategoryService {
	

	public CategoryServiceImpl() {
	}

	@Override
	public List<Category> getCategoriesByReferences(List<CategoryReference> categoryReferenceList) {
		if (categoryReferenceList == null || categoryReferenceList.isEmpty()) {
			return Collections.emptyList();
		}

		Set<Long> categoryIds = categoryReferenceList.stream().map(CategoryReference::getId).collect(Collectors.toSet());
		List<Category> results = new ArrayList<>();
		//categoryRepository.findAllById(categoryIds).forEach(results::add);
		return results;
	}

	@Override
	public Optional<Category> getCategoryByReference(CategoryReference categoryReference) {
		if (categoryReference == null || categoryReference.getId() == null) {
			return Optional.empty();
		}
		//return categoryRepository.findById(categoryReference.getId());
		return null;
	}

	@Override
	public Optional<Category> getCategory(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		//return categoryRepository.findByCode(code);
		return null;
	}

	@Override
	public Category saveCategory(Category category) {
		ArgumentAssert.notNull(category, "category");
		ArgumentAssert.notNullOrEmpty(category.getCode(), "code");
		ArgumentAssert.notNull(category.getTaxonomyReference(), "taxonomy reference");
		ArgumentAssert.notNullOrEmpty(category.getTaxonomyReference().getId(), "taxonomy reference ID");

		//return categoryRepository.save(category);
		return null;
	}

	@Override
	public void deleteCategory(Category category) {
		ArgumentAssert.notNull(category, "category");
		ArgumentAssert.notNull(category.getId(), "ID");
//		categoryRepository.delete(category);
	}
}
