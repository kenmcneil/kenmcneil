package com.ferguson.cs.productapi.attribute;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.model.attribute.AttributeDefinition;

public interface AttributeDefinitionRepository extends MongoRepository<AttributeDefinition, String> {

}
