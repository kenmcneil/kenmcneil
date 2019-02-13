package com.ferguson.cs.product.api.taxonomy;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.taxonomy.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
	Optional<Category> findByCode(String code);
}
