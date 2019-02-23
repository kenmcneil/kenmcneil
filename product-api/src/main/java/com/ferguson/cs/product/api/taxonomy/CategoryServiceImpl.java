package com.ferguson.cs.product.api.taxonomy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ferguson.cs.model.taxonomy.Category;
import com.ferguson.cs.model.taxonomy.CategoryReference;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> getCategoriesByReferences(List<CategoryReference> categoryReferenceList) {
		if (categoryReferenceList == null || categoryReferenceList.isEmpty()) {
			return Collections.emptyList();
		}

		Set<String> categoryIds = categoryReferenceList.stream().map(CategoryReference::getId).collect(Collectors.toSet());
		List<Category> results = new ArrayList<>();
		categoryRepository.findAllById(categoryIds).forEach(results::add);
		return results;
	}

	@Override
	public Optional<Category> getCategoryByReference(CategoryReference categoryReference) {
		if (categoryReference == null || StringUtils.hasText(categoryReference.getId())) {
			return Optional.empty();
		}
		return categoryRepository.findById(categoryReference.getId());
	}

	@Override
	public Optional<Category> getCategory(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		return categoryRepository.findByCode(code);
	}

	@Override
	public Category saveCategory(Category category) {
		ArgumentAssert.notNull(category, "category");
		ArgumentAssert.notNullOrEmpty(category.getCode(), "code");
		ArgumentAssert.notNull(category.getTaxonomyReference(), "taxonomy reference");
		ArgumentAssert.notNullOrEmpty(category.getTaxonomyReference().getId(), "taxonomy reference ID");

		return categoryRepository.save(category);
	}

	@Override
	public void deleteCategory(Category category) {
		ArgumentAssert.notNull(category, "category");
		ArgumentAssert.notNullOrEmpty(category.getId(), "ID");
		categoryRepository.delete(category);
	}
}
