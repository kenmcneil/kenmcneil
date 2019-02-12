package com.ferguson.cs.product.api.taxonomy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.taxonomy.Taxonomy;

public interface TaxonomyRepository extends MongoRepository<Taxonomy, String> {

}
