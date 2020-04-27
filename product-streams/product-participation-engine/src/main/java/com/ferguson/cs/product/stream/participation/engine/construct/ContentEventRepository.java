package com.ferguson.cs.product.stream.participation.engine.construct;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferguson.cs.product.stream.participation.engine.model.ContentEvent;

public interface ContentEventRepository extends MongoRepository<ContentEvent, Integer>  {
}
