package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Date;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

@Repository
public class ParticipationItemRepositoryImpl implements ParticipationItemRepositoryCustom {
	private final String PARTICIPATION_ITEM_COLLECTION_NAME = "participationItem";

	private MongoOperations coreMongoTemplate;
	private ParticipationEngineSettings participationEngineSettings;

	ParticipationItemRepositoryImpl(
			MongoOperations coreMongoTemplate,
			ParticipationEngineSettings participationEngineSettings
	) {
		this.coreMongoTemplate = coreMongoTemplate;
		this.participationEngineSettings = participationEngineSettings;
	}

	/**
	 * Get a published ParticipationItem that's marked as ready to be published to the
	 * engine. Returns the full record since it's needed for inserting to SQL tables.
	 */
	@Override
	public ParticipationItem getNextPendingPublishParticipation(Integer minParticipationId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(ParticipationItemStatus.PUBLISHED));
		query.addCriteria(Criteria.where("updateStatus").is(ParticipationItemUpdateStatus.NEEDS_PUBLISH));
		query.with(Sort.by(Sort.Direction.ASC, "lastModifiedDate"));

		// For development / load testing.
		if (minParticipationId != null) {
			query.addCriteria(Criteria.where("_id").gte(minParticipationId));
		}

		return coreMongoTemplate.findOne(query, ParticipationItem.class, PARTICIPATION_ITEM_COLLECTION_NAME);
	}

	/**
	 * This returns a partial object with only id and lastModifiedUserId,
	 * since those are the only needed values.
	 */
	@Override
	public ParticipationItem getNextPendingUnpublishParticipation(Integer minParticipationId) {
		Query query = new Query();
		query.fields().include("id");
		query.fields().include("lastModifiedUserId");
		query.addCriteria(Criteria.where("status").is(ParticipationItemStatus.PUBLISHED));
		query.addCriteria(Criteria.where("updateStatus").is(ParticipationItemUpdateStatus.NEEDS_UNPUBLISH));

		// For development / load testing.
		if (minParticipationId != null) {
			query.addCriteria(Criteria.where("_id").gte(minParticipationId));
		}

		return coreMongoTemplate.findOne(query, ParticipationItem.class, PARTICIPATION_ITEM_COLLECTION_NAME);
	}

	@Override
	public void updateParticipationItemStatus(
			int participationId,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus,
			int userId,
			Date processingDate
	) {
		Update update = new Update();
		update.set("status", status);
		update.set("updateStatus", updateStatus);
		update.set("lastModifiedUserId", userId);
		update.set("lastModifiedDate", processingDate);
		coreMongoTemplate.updateFirst(
				Query.query(Criteria.where("_id").is(participationId)),
				update,
				PARTICIPATION_ITEM_COLLECTION_NAME
		);
	}
}
