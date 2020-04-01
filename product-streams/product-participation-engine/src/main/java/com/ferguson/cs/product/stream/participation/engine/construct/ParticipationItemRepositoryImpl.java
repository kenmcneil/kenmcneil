package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.model.PagedSearchResults;
import com.ferguson.cs.product.stream.participation.engine.model.Paging;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSearchCriteria;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.product.stream.participation.engine.model.SortedPagedSearchCriteria;

@Repository
public class ParticipationItemRepositoryImpl implements ParticipationItemRepositoryCustom {
	private final String PARTICIPATION_ITEM_COLLECTION_NAME = "participationItem";

	private ParticipationEngineSettings participationEngineSettings;
	private MongoOperations coreMongoTemplate;

	@Autowired
	public void setParticipationEngineSettings(ParticipationEngineSettings participationEngineSettings) {
		this.participationEngineSettings = participationEngineSettings;
	}

	@Autowired
	public void setMongoOperations(MongoOperations coreMongoTemplate) {
		this.coreMongoTemplate = coreMongoTemplate;
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
		if (!ParticipationItemStatus.PUBLISHED.equals(status)) {
			update.set("status", status);
		}
		update.set("updateStatus", updateStatus);
		update.set("lastModifiedUserId", userId);
		update.set("lastModifiedDate", processingDate);
		coreMongoTemplate.updateFirst(
				Query.query(Criteria.where("_id").is(participationId)),
				update,
				PARTICIPATION_ITEM_COLLECTION_NAME
		);
	}

	@Override
	public PagedSearchResults<ParticipationItem> findMatchingParticipationItems(ParticipationItemSearchCriteria criteria) {
		Query query = new Query();

		if (criteria.getId() != null) {
			query.addCriteria(Criteria.where("_id").is(criteria.getId()));
		}
		else {
			if (criteria.getSaleId() != null) {
				query.addCriteria(Criteria.where("saleId").is(criteria.getSaleId()));
			}

			if (StringUtils.isNotBlank(criteria.getDescription())) {
				query.addCriteria(Criteria.where("description").regex(Pattern.quote(criteria.getDescription()), "i"));
			}

			if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
				query.addCriteria(Criteria.where("status").in(criteria.getStatuses()
						.stream()
						.map(ParticipationItemStatus::toString)
						.collect(Collectors.toList())
				));
			}

			if (criteria.getStarringUserIds() != null && !criteria.getStarringUserIds().isEmpty()) {
				query.addCriteria(Criteria.where("starringUserIds").in(criteria.getStarringUserIds()));
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

		return findMatchingContent(criteria, query, ParticipationItem.class, PARTICIPATION_ITEM_COLLECTION_NAME);
	}

	private <T> PagedSearchResults<T> findMatchingContent(SortedPagedSearchCriteria criteria, Query query, Class<T> className, String collectionName) {
		// GET TOTAL COUNT FOR PAGINATION
		long total = coreMongoTemplate.count(query, collectionName);
		int skip = Integer.max(0, (criteria.getPage() - 1) * criteria.getPageSize());

		// GET ACTUAL RECORDS
		List<T> result;
		if (total > skip) {
			if (criteria.getSortColumn() != null && criteria.getSortOrder() != null) {
				Sort sort = new Sort(Sort.Direction.fromString(criteria.getSortOrder().toString()), criteria.getSortColumn().getColumnName());
				query.with(sort);
			}

			query.skip(skip);
			query.limit(criteria.getPageSize());

			result = coreMongoTemplate.find(query, className, collectionName);
		}
		else {
			result = new ArrayList<>();
		}

		Paging paging = calculatePaging((int) total, criteria.getPage(), criteria.getPageSize());

		return new PagedSearchResults<>(paging, result);
	}

	private Paging calculatePaging(int resultCount, int page, int pageSize) {
		int pages = 0;
		if (resultCount > 0 && pageSize > 0) {
			pages = (int) Math.ceil(resultCount / (double) pageSize);
		}

		if (page < 1) {
			page = 1;
		}
		else if (page > pages) {
			page = pages;
		}
		return new Paging(page, pageSize, resultCount, pages);
	}
}
