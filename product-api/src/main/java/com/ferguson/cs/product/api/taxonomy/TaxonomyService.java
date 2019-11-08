package com.ferguson.cs.product.api.taxonomy;

import java.util.List;
import java.util.Optional;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyCategory;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryCriteria;
import com.ferguson.cs.model.taxonomy.TaxonomyCategoryReference;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;

public interface TaxonomyService {

	Optional<Taxonomy> getTaxonomyByReference(TaxonomyReference taxonomyReference);
	Optional<Taxonomy> getTaxonomyById(Integer code);
	Optional<Taxonomy> getTaxonomyByCode(String code);

	Taxonomy saveTaxonomy(Taxonomy taxonomy);
	void deleteTaxonomy(Taxonomy taxonomy);

	List<TaxonomyCategory> findCategoryList(TaxonomyCategoryCriteria criteria);
	Optional<TaxonomyCategory> getCategoryByReference(TaxonomyCategoryReference categoryReference);
	Optional<TaxonomyCategory> getCategoryById(Long catgoryId);
	TaxonomyCategory saveCategory(TaxonomyCategory category);
	void deleteCategory(TaxonomyCategory category);
}
