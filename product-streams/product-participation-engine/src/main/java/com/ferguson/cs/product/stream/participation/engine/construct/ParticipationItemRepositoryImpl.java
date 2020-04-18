package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSearchCriteria;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.utilities.ArgumentAssert;

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

	/**
	 * Note that this returns a partial object with only id and lastModifiedUserId,
	 * since those are the only needed values.
	 */
	@Override
	@Nullable
	public ParticipationItem findParticipationItemEvent(@NonNull ParticipationItemSearchCriteria criteria) {
		ArgumentAssert.notNull(criteria, "criteria");
		Query query = new Query();
		query.fields().include("id");
		query.fields().include("lastModifiedUserId");

		if (criteria.getId() != null) {
			query.addCriteria(Criteria.where("_id").is(criteria.getId()));
		}
		else {
			if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
				query.addCriteria(Criteria.where("status").in(criteria.getStatuses()
						.stream()
						.map(ParticipationItemStatus::toString)
						.collect(Collectors.toList())
				));
			}

			if (criteria.getUpdateStatus() != null) {
				query.addCriteria(Criteria.where("updateStatus").is(criteria.getUpdateStatus().toString()));
			}

			if (BooleanUtils.isTrue(criteria.getIsExpired())) {
				query.addCriteria(
						new Criteria().andOperator(
								Criteria.where("schedule.to").exists(true),
								Criteria.where("schedule.to").lt(new Date())));

			}
			else {
				Date scheduledOn = criteria.getScheduledOn();
				if (scheduledOn != null) {
					query.addCriteria(
							new Criteria().andOperator(
									new Criteria().orOperator(
											new Criteria().andOperator(
													Criteria.where("schedule.from").exists(true),
													Criteria.where("schedule.from").lte(scheduledOn)),
											new Criteria().andOperator(
													Criteria.where("schedule.from").exists(false),
													Criteria.where("lastModifiedDate").lte(scheduledOn))),
									new Criteria().orOperator(
											new Criteria().andOperator(
													Criteria.where("schedule.to").exists(true),
													Criteria.where("schedule.to").gt(scheduledOn)),
											Criteria.where("schedule.to").exists(false))));
				}
			}
		}

		// For development / load testing.
		if (participationEngineSettings.getTestModeEnabled()) {
			query.addCriteria(Criteria.where("_id").gte(participationEngineSettings.getTestModeMinParticipationId()));
		}

		return coreMongoTemplate.findOne(query, ParticipationItem.class, PARTICIPATION_ITEM_COLLECTION_NAME);
	}
}
