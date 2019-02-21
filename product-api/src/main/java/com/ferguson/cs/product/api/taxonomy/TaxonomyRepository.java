package com.ferguson.cs.product.api.taxonomy;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.taxonomy.Taxonomy;

public interface TaxonomyRepository extends MongoRepository<Taxonomy, String> {
	Optional<Taxonomy> findByCode(String code);
}
