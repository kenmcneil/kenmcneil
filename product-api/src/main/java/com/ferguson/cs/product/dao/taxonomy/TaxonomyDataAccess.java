package com.ferguson.cs.product.dao.taxonomy;

import java.util.List;
import java.util.Optional;

import com.ferguson.cs.model.IdCodeCriteria;
import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;

public interface TaxonomyDataAccess {

	Optional<Taxonomy> getTaxonomyById(Integer id);
	Optional<Taxonomy> getTaxonomyByCode(String code);
	List<Taxonomy> findTaxonomies(IdCodeCriteria criteria);
	Taxonomy saveTaxonomy(Taxonomy taxonomy);
	void deleteTaxonomy(Taxonomy taxonomy);

	Optional<TaxonomyCategory> getTaxonomyCategoryById(Long id);
	List<TaxonomyCategory> findCategories(TaxonomyCategoryCriteria criteria);
	TaxonomyCategory saveCategory(TaxonomyCategory category);
	void deleteCategory(TaxonomyCategory category);

}
