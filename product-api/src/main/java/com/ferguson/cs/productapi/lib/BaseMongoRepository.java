package com.ferguson.cs.productapi.lib;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

public class BaseMongoRepository<T> extends SimpleMongoRepository<T, String> {

	public BaseMongoRepository(MongoEntityInformation<T, String> metadata, MongoOperations mongoOperations) {
		super(metadata, mongoOperations);
	}

}
