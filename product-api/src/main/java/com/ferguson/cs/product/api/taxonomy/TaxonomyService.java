package com.ferguson.cs.product.api.taxonomy;

import java.util.List;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;

public interface TaxonomyService {

	List<Taxonomy> getTaxonomiesByReference(List<TaxonomyReference> taxonomyReferenceList);

}
