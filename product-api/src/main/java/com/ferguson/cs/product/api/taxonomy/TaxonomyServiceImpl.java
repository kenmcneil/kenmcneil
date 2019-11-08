package com.ferguson.cs.product.api.taxonomy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.data.DataEntityHelper;
import com.ferguson.cs.model.attribute.AttributeDefinition;
import com.ferguson.cs.model.attribute.AttributeDefinitionCriteria;
import com.ferguson.cs.model.attribute.AttributeDefinitionReference;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryAttribute;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;
import com.ferguson.cs.product.api.attribute.AttributeService;
import com.ferguson.cs.product.dao.taxonomy.TaxonomyDataAccess;
import com.ferguson.cs.server.common.response.exception.ResourceNotFoundException;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

	private final TaxonomyDataAccess taxonomyDataAccess;
	private final AttributeService attributeService;
	private final DataEntityHelper dataEntityHelper;

	public TaxonomyServiceImpl(TaxonomyDataAccess taxonomyDataAccess, AttributeService attributeService, DataEntityHelper dataEntityHelper) {
		this.taxonomyDataAccess = taxonomyDataAccess;
		this.attributeService = attributeService;
		this.dataEntityHelper = dataEntityHelper;
	}

	@Override
	public Optional<Taxonomy> getTaxonomyByReference(TaxonomyReference taxonomyReference) {

		ArgumentAssert.notNull(taxonomyReference, "taxonomyReference");
		return taxonomyDataAccess.getTaxonomyById(taxonomyReference.getId());
	}

	@Override
	public Optional<Taxonomy> getTaxonomyByCode(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
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


		//Retrieve the taxonomy by the ID if the ID was provided, otherwise use the code.
		Optional<Taxonomy> retrievedTaxonomy = getTaxonomyByReference(new TaxonomyReference(taxonomy));

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
	public Optional<TaxonomyCategory> getCategoryByReference(TaxonomyCategoryReference categoryReference) {
		ArgumentAssert.notNull(categoryReference, "categoryReference");

		return getCategoryById(categoryReference.getId());
	}

	@Override
	public Optional<TaxonomyCategory> getCategoryById(Long categoryId) {
		ArgumentAssert.notNull(categoryId, "categoryId");
		Optional<TaxonomyCategory> category= taxonomyDataAccess.getTaxonomyCategoryById(categoryId);
		if (category.isPresent() && !CollectionUtils.isEmpty(category.get().getAttributes())) {
			//The retrieve of the taxonomy's attributes will only populate the attribute definition's IDs. We need to resolve
			//those to a full attribute definition reference.
			Map<Integer, AttributeDefinitionReference> definitionMap = findAttributeDefintions(category.get().getAttributes());
			for (TaxonomyCategoryAttribute attribute : category.get().getAttributes()) {
				if (attribute.getDefinition() != null || attribute.getDefinition().getId() != null) {
					AttributeDefinitionReference definition = definitionMap.get(attribute.getDefinition().getId());
					if (definition != null) {
						attribute.setDefinition(definition);
					}
				}
			}
		}
		return category;
	}

	/**
	 * This method will accept a list of taxonomy category attributes and find all the attribute definitions for the category attributes.
	 *
	 * @param attributes The category attributes
	 * @return A map of attribute definition IDs to attribute definition references.
	 */
	private Map<Integer, AttributeDefinitionReference> findAttributeDefintions(List<TaxonomyCategoryAttribute> attributes) {

		if (CollectionUtils.isEmpty(attributes)) {
			return Collections.emptyMap();
		}

		//Collect the Ids of all the definitions.
		Set<Integer> attributeDefinitionIds = attributes.stream()
				.filter(definition -> definition.getDefinition() != null && definition.getDefinition().getId() != null)
				.map(definition -> definition.getDefinition().getId()).collect(Collectors.toSet());

		List<AttributeDefinition> attributeDefinitions = attributeService.findAttributeDefinitions(AttributeDefinitionCriteria.builder()
			.attributeDefinitionIds(attributeDefinitionIds)
			.build());

		//Convert the results into a map of ID -> attributeDefinition References.
		return attributeDefinitions.stream().collect(Collectors.toMap(AttributeDefinition::getId, AttributeDefinitionReference::new));
	}

	@Override
	public List<TaxonomyCategory> findCategoryList(TaxonomyCategoryCriteria criteria) {
		ArgumentAssert.notNull(criteria, "criteria");

		if (CollectionUtils.isEmpty(criteria.getCategoryIds()) &&
				criteria.getCategoryIdParent() == null && (
					StringUtils.isEmpty(criteria.getCategoryPath()) ||
					(!StringUtils.isEmpty(criteria.getCategoryPath()) &&
						criteria.getTaxonomyId() == null && StringUtils.isEmpty(criteria.getTaxonomyCode())))) {

			throw new IllegalArgumentException("You must supply one of the follow to search for categories: " +
					"A least one category ID, the categoryIdParent, or a category path accompanied with either the taxonomy ID or taxonomy Code.");
		}
		List<TaxonomyCategory> categories = taxonomyDataAccess.findCategories(criteria);

		//The references to an attribute definition are NOT populated via the findCategory query because the attribute definitions are in a different aggregate root.
		//We do want the fully populated references, and therefore we make a call out to the attribute service to retrieve them. We are making
		//one aggregate "find" for all attributes. My one concern here is when retrieving a large number of categories (via a category parent or just a really big list of category IDs)
		//we may exceed the number of allowed element in an "in clause". For now, just leaving this alone, but we could either just chunk the number of attributes in that case or
		//go to an ID -> cache model.
		if (!categories.isEmpty()) {
			List<TaxonomyCategoryAttribute> allAttributes = categories.stream()
					.map(category -> category.getAttributes()) //TaxonomyCategory -> List<List<TaxonomyCategoryAttribue>>
					.flatMap(Collection::stream) //Flatten to one stream.
					.collect(Collectors.toList()); //And collect.

			Map<Integer, AttributeDefinitionReference> definitionMap = findAttributeDefintions(allAttributes);
			for (TaxonomyCategoryAttribute attribute : allAttributes) {
				//The attributes are modified by reference, no need to put them "back" into our list of categories.
				if (attribute.getDefinition() != null || attribute.getDefinition().getId() != null) {
					AttributeDefinitionReference definition = definitionMap.get(attribute.getDefinition().getId());
					if (definition != null) {
						attribute.setDefinition(definition);
					}
				}
			}
		}
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
			ArgumentAssert.notNull(category.getCategoryParent().getPath(), "category.categoryParent.path");

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
