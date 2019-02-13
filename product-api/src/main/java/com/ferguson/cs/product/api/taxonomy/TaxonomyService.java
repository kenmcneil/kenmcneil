package com.ferguson.cs.product.api.taxonomy;

import java.util.List;
import java.util.Optional;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;

public interface TaxonomyService {

	List<Taxonomy> getTaxonomiesByReferences(List<TaxonomyReference> taxonomyReferenceList);
	Optional<Taxonomy> getTaxonomyByReference(TaxonomyReference taxonomyReference);
	Optional<Taxonomy> getTaxonomy(String code);
	Taxonomy saveTaxonomy(Taxonomy taxonomy);
	void deleteTaxonomy(Taxonomy taxonomy);

}
