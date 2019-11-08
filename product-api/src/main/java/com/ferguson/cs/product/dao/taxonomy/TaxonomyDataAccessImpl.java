package com.ferguson.cs.product.dao.taxonomy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.data.AbstractDataAccess;
import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryAttribute;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;
import com.ferguson.cs.utilities.ArgumentAssert;

@Repository
public class TaxonomyDataAccessImpl extends AbstractDataAccess implements TaxonomyDataAccess {

	private TaxonomyMapper taxonomyMapper;

	public TaxonomyDataAccessImpl(TaxonomyMapper taxonomyMapper) {
		this.taxonomyMapper = taxonomyMapper;
	}

	@Override
	public Optional<Taxonomy> getTaxonomyById(Integer id) {

		ArgumentAssert.notNull(id, "id");

		return taxonomyMapper.findTaxonomies(
				IdCodeCriteria.builder()
					.id(id)
					.build()
				).stream().findFirst();
	}

	@Override
	public Optional<Taxonomy> getTaxonomyByCode(String code) {

		ArgumentAssert.notNullOrEmpty(code, "code");

		return taxonomyMapper.findTaxonomies(
				IdCodeCriteria.builder()
					.code(code)
					.build()
				).stream().findFirst();
	}

	@Override
	public List<Taxonomy> findTaxonomies(IdCodeCriteria criteria) {
		return taxonomyMapper.findTaxonomies(criteria);
	}

	@Override
	public Taxonomy saveTaxonomy(Taxonomy taxonomy) {
		boolean isNew = isNew(taxonomy);

		Taxonomy savedTaxonomy = saveEntity(taxonomy, taxonomyMapper::insertTaxonomy,  taxonomyMapper::updateTaxonomy);

		if (isNew) {

			//Create and insert the root Category.
			TaxonomyCategory rootCategory = TaxonomyCategory.builder()
					.code(taxonomy.getCode())
					.taxonomy(new TaxonomyReference(taxonomy))
					.description(taxonomy.getDescription())
					.path("")
					.name("ROOT")
					.build();
			rootCategory = saveEntity(rootCategory, taxonomyMapper::insertCategory, taxonomyMapper::updateCategory);
			savedTaxonomy.setRootCategory(new TaxonomyCategoryReference(rootCategory));
			taxonomyMapper.linkTaxonomyRootCategory(savedTaxonomy);
		}

		return savedTaxonomy;
	}

	@Override
	@Transactional
	public void deleteTaxonomy(Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		ArgumentAssert.notNull(taxonomy.getId(), "taxonomy.id");
		ArgumentAssert.notNull(taxonomy.getVersion(), "taxonomy.version");
		ArgumentAssert.notNull(taxonomy.getRootCategory(), "taxonomy.");
		ArgumentAssert.notNull(taxonomy.getRootCategory().getId(), "taxonomy.rootCategory.id");

		//And then delete the taxonomy.
		deleteEntity(taxonomy, taxonomyMapper::deleteTaxonomy);
	}

	@Override
	public Optional<TaxonomyCategory> getTaxonomyCategoryById(Long id) {
		ArgumentAssert.notNull(id, "id");

		return taxonomyMapper.findCategories(
				TaxonomyCategoryCriteria.builder()
					.categoryIds(Collections.singleton(id))
					.build()
				).stream().findFirst();
	}

	@Override
	public List<TaxonomyCategory> findCategories(TaxonomyCategoryCriteria criteria) {
		return taxonomyMapper.findCategories(criteria);
	}

	@Override
	@Transactional
	public TaxonomyCategory saveCategory(TaxonomyCategory category) {

		boolean isNew = isNew(category);

		TaxonomyCategory savedCategory = saveEntity(category, taxonomyMapper::insertCategory, taxonomyMapper::updateCategory);

		//save attributes.
		if (category.getAttributes() != null) {
			for (TaxonomyCategoryAttribute attribute : category.getAttributes()) {
				saveChildEntity(attribute, savedCategory, taxonomyMapper::insertCategoryAttribute, taxonomyMapper::updateCategoryAttribute);
			}
		}

		if (!isNew) {
			//Delete any orphans attributes that are not in the current, in-memory list.
			taxonomyMapper.deleteCatagoryAttributes(category, savedCategory.getAttributes());
		}

		return savedCategory;
	}

	@Override
	@Transactional
	public void deleteCategory(TaxonomyCategory category) {

		//delete attribute links
		taxonomyMapper.deleteCatagoryAttributes(category, null);

		deleteEntity(category, taxonomyMapper::deleteCategory);
	}


}
