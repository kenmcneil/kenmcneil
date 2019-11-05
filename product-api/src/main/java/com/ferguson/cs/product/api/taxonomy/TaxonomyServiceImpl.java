package com.ferguson.cs.product.api.taxonomy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ferguson.cs.data.DataEntityHelper;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;
import com.ferguson.cs.product.dao.taxonomy.TaxonomyDataAccess;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

	private final TaxonomyDataAccess taxonomyDataAccess;
	private final DataEntityHelper dataEntityHelper;

	public TaxonomyServiceImpl(TaxonomyDataAccess taxonomyDataAccess, DataEntityHelper dataEntityHelper) {
		this.taxonomyDataAccess = taxonomyDataAccess;
		this.dataEntityHelper = dataEntityHelper;
	}

	@Override
	public Optional<Taxonomy> getTaxonomyByReference(TaxonomyReference taxonomyReference) {

		ArgumentAssert.notNull(taxonomyReference, "taxonomyReference");
		return taxonomyDataAccess.getTaxonomyById(taxonomyReference.getId());
	}

	@Override
	public Optional<Taxonomy> getTaxonomyByCode(String code) {
		return taxonomyDataAccess.getTaxonomyByCode(code);
	}

	@Override
	public Optional<Taxonomy> getTaxonomyById(Integer id) {
		return taxonomyDataAccess.getTaxonomyById(id);
	}

	@Override
	@Transactional
	public Taxonomy saveTaxonomy(Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		ArgumentAssert.notNullOrEmpty(taxonomy.getCode(), "code");
		ArgumentAssert.notNullOrEmpty(taxonomy.getDescription(), "description");

		if (dataEntityHelper.isNew(taxonomy) && taxonomy.getRootCategory() != null) {
			//You cannot explicitly set the root catagory on a new taxonomy, it is created automatically.
			throw new IllegalArgumentException("The root category will be implicitly created for a new taxonomy. You cannot override this behavior.");
		}

		return taxonomyDataAccess.saveTaxonomy(taxonomy);
	}

	@Override
	@Transactional
	public void deleteTaxonomy(Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		ArgumentAssert.notNull(taxonomy.getId(), "taxonomy.id");
		ArgumentAssert.notNull(taxonomy.getVersion(), "taxonomy.version");

		if (taxonomy.getId() == null && !StringUtils.hasText(taxonomy.getCode())) {
			throw new IllegalArgumentException("The taxonomy ID or code is required when deleting the taxonomy.");
		}

		//Retrieve the taxonomy by the ID if the ID was provided, otherwise use the code.
		Optional<Taxonomy> retrievedTaxonomy = taxonomy.getId() !=null?
				getTaxonomyById(taxonomy.getId()):getTaxonomyByCode(taxonomy.getCode());

		if (!retrievedTaxonomy.isPresent()) {
			throw new ResourceNotFoundException("The taxonomy does not exist.");
		}

		//We check the version of the taxonomy BEFORE we delete all the categories.
		if (!taxonomy.getVersion().equals(retrievedTaxonomy.get().getVersion())) {
			throw new OptimisticLockingFailureException("Optimistic lock exception while deleting entity of type Taxonomy");
		}

		//This will also recursively delete the taxonomy's category tree.
		Optional<TaxonomyCategory> rootCategory = getCategoryByReference(taxonomy.getRootCategory());
		if (rootCategory.isPresent()) {
			deleteCategory(rootCategory.get());
		}

		//The data access will unlink the root category from the taxonomy and delete the taxonomy.
		taxonomyDataAccess.deleteTaxonomy(retrievedTaxonomy.get());


		//Fire "DeleteTaxonomyEvent"
	}

	@Override
	public List<TaxonomyCategory> getCategoriesByReferences(List<TaxonomyCategoryReference> categoryReferenceList) {
		if (categoryReferenceList == null || categoryReferenceList.isEmpty()) {
			return Collections.emptyList();
		}

		Set<Long> categoryIds = categoryReferenceList.stream().map(TaxonomyCategoryReference::getId).collect(Collectors.toSet());

		TaxonomyCategoryCriteria criteria = TaxonomyCategoryCriteria.builder()
				.categoryIds(categoryIds)
				.build();
		return taxonomyDataAccess.findCategories(criteria);
	}

	@Override
	public Optional<TaxonomyCategory> getCategoryByReference(TaxonomyCategoryReference categoryReference) {
		ArgumentAssert.notNull(categoryReference, "categoryReference");
		return taxonomyDataAccess.getTaxonomyCategoryById(categoryReference.getId());
	}

	@Override
	public Optional<TaxonomyCategory> getCategoryById(Long categoryId) {
		ArgumentAssert.notNull(categoryId, "categoryId");
		return taxonomyDataAccess.getTaxonomyCategoryById(categoryId);
	}

	@Override
	public List<TaxonomyCategory> findCategoryList(TaxonomyCategoryCriteria criteria) {
		return taxonomyDataAccess.findCategories(criteria);
	}

	@Override
	@Transactional
	public TaxonomyCategory saveCategory(TaxonomyCategory category) {
		ArgumentAssert.notNull(category, "category");
		ArgumentAssert.notNullOrEmpty(category.getName(), "category.name");
		ArgumentAssert.notNullOrEmpty(category.getDescription(), "category.description");

		boolean isNew = dataEntityHelper.isNew(category);

		if (isNew) {
			//A category's place in the taxonomy is set on insert and is immutable after it has been saved. The only want to
			//make a change that impacts where the category exists within the taxonomy category is to create a new category,
			//copy over settings, and then delete the old category.
			ArgumentAssert.notNullOrEmpty(category.getCode(), "code");
			ArgumentAssert.notNull(category.getCategoryParent(), "category.categoryParent");
			ArgumentAssert.notNull(category.getCategoryParent().getId(), "category.categoryParent.id");
			ArgumentAssert.notNullOrEmpty(category.getCategoryParent().getPath(), "category.categoryParent.path");

			//if the taxonomy is not provided, attempt to derive it from the parent.
			if (category.getTaxonomy() == null || category.getTaxonomy().getId() == null) {
				category.setTaxonomy(category.getCategoryParent().getTaxonomy());
			}
			//Make sure the taxonomy is present!
			ArgumentAssert.notNull(category.getTaxonomy(), "category.taxonomy");
			ArgumentAssert.notNull(category.getTaxonomy().getId(), "category.taxonomy.id");

			if (category.getCode().indexOf('.') > -1) {
				throw new IllegalArgumentException("The category code may not contain a decimal '.'.");
			}

			//The path of the category is ALWAYS derived from the parent's path and the category's code.
			StringBuilder path = new StringBuilder(category.getCategoryParent().getPath());
			if (!category.getCategoryParent().getPath().isEmpty()) {
				path.append(".");
			}
			path.append(category.getCode());
			category.setPath(path.toString());
		}

		return taxonomyDataAccess.saveCategory(category);
		//Fire TaxonomyCategoryCreatedEvent or TaxonomyCategoryUpdatedEvent
	}

	@Override
	@Transactional
	public void deleteCategory(TaxonomyCategory category) {
		ArgumentAssert.notNull(category, "category");
		ArgumentAssert.notNull(category.getId(), "category.id");
		ArgumentAssert.notNull(category.getVersion(), "category.version");

		//find all child categories and delete those first. This is recursive!
		List<TaxonomyCategory> children = findCategoryList(TaxonomyCategoryCriteria.builder()
				.categoryIdParent(category.getId())
				.build());
		children.stream().forEach(this::deleteCategory);

		taxonomyDataAccess.deleteCategory(category);

		//Publish TaxonomyCategoryDeleteEvent
	}

}
