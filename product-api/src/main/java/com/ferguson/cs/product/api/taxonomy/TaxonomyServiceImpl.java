package com.ferguson.cs.product.api.taxonomy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ferguson.cs.model.taxonomy.Taxonomy;
import com.ferguson.cs.model.taxonomy.TaxonomyReference;
import com.ferguson.cs.utilities.ArgumentAssert;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

	private final TaxonomyRepository taxonomyRepository;

	public TaxonomyServiceImpl(TaxonomyRepository taxonomyRepository) {
		this.taxonomyRepository = taxonomyRepository;
	}

	@Override
	public List<Taxonomy> getTaxonomiesByReferences(List<TaxonomyReference> taxonomyReferenceList) {

		if (taxonomyReferenceList == null || taxonomyReferenceList.isEmpty()) {
			return Collections.emptyList();
		}

		Set<String> taxonomyIds = taxonomyReferenceList.stream().map(TaxonomyReference::getId).collect(Collectors.toSet());
		List<Taxonomy> results = new ArrayList<>();
		taxonomyRepository.findAllById(taxonomyIds).forEach(results::add);
		return results;
	}

	@Override
	public Optional<Taxonomy> getTaxonomyByReference(TaxonomyReference taxonomyReference) {
		if (taxonomyReference == null || StringUtils.hasText(taxonomyReference.getId())) {
			return Optional.empty();
		}
		return taxonomyRepository.findById(taxonomyReference.getId());
	}

	@Override
	public Optional<Taxonomy> getTaxonomy(String code) {
		ArgumentAssert.notNullOrEmpty(code, "code");
		return taxonomyRepository.findByCode(code);
	}

	@Override
	public Taxonomy saveTaxonomy(Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		ArgumentAssert.notNullOrEmpty(taxonomy.getCode(), "code");
		return taxonomyRepository.save(taxonomy);
	}

	@Override
	public void deleteTaxonomy(Taxonomy taxonomy) {
		ArgumentAssert.notNull(taxonomy, "taxonomy");
		ArgumentAssert.notNullOrEmpty(taxonomy.getId(), "ID");
		taxonomyRepository.delete(taxonomy);
	}

}
