package com.ferguson.cs.product.api.attribute;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.attribute.AttributeDefinition;

public interface AttributeDefinitionRepository extends MongoRepository<AttributeDefinition, String> {

	Optional<AttributeDefinition> findByCode(String code);

}
