package com.ferguson.cs.product.api.attribute;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.attribute.UnitOfMeasure;

public interface UnitOfMeasureRepository extends MongoRepository<UnitOfMeasure, String> {

	Optional<UnitOfMeasure> findByCode(String code);
}
