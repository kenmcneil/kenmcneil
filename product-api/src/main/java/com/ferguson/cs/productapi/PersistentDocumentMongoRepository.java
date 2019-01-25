package com.ferguson.cs.productapi;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.ferguson.cs.model.PersistentDocument;

public class PersistentDocumentMongoRepository<T extends PersistentDocument> extends SimpleMongoRepository <T, String> {

	public PersistentDocumentMongoRepository(MongoEntityInformation<T, String> metadata, MongoOperations mongoOperations) {
		super(metadata, mongoOperations);
	}

	@Override
	public <S extends T> S save(S entity) {
		sanitizeId(entity);
		return super.save(entity);
	}


	@Override
	public <S extends T> List<S> saveAll(Iterable<S> entities) {

		Assert.notNull(entities, "The given Iterable of entities not be null!");
		Streamable.of(entities).stream().forEach(this::sanitizeId);
		return super.saveAll(entities);
	}


	@Override
	public <S extends T> S insert(S entity) {
		sanitizeId(entity);
		return super.insert(entity);
	}

	@Override
	public <S extends T> List<S> insert(Iterable<S> entities) {
		Assert.notNull(entities, "The given Iterable of entities not be null!");
		Streamable.of(entities).stream().forEach(this::sanitizeId);
		return super.insert(entities);
	}

	private void sanitizeId(T entity) {
		if (!StringUtils.hasText(entity.getId())) {
			entity.setId(null);
		}
	}
}
