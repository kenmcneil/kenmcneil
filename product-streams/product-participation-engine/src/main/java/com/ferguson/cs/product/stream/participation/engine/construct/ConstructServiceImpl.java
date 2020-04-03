package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Collections;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.model.ContentEvent;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSearchCriteria;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

@Service
public class ConstructServiceImpl implements ConstructService {

	private final ParticipationEngineSettings participationEngineSettings;
	private final ContentEventRepository contentEventRepository;
	private final ParticipationItemRepository participationItemRepository;

	public ConstructServiceImpl(
			ParticipationEngineSettings participationEngineSettings,
			ContentEventRepository contentEventRepository,
			ParticipationItemRepository participationItemRepository
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.contentEventRepository = contentEventRepository;
		this.participationItemRepository = participationItemRepository;
	}

	@Override
	public ParticipationItem getNextPendingActivationParticipation() {
		ParticipationItemSearchCriteria criteria = createSearchCriteria(
				ParticipationItemUpdateStatus.NEEDS_UPDATE, false);
		criteria.setScheduledOn(new Date());
		return participationItemRepository.findMatchingParticipationItem(criteria);
	}

	@Override
	public ParticipationItem getNextPendingDeactivationParticipation() {
		ParticipationItemSearchCriteria criteria = createSearchCriteria(null, true);
		return participationItemRepository.findMatchingParticipationItem(criteria);
	}

	@Override
	public ParticipationItem getNextPendingUnpublishParticipation() {
		ParticipationItemSearchCriteria criteria = createSearchCriteria(ParticipationItemUpdateStatus.NEEDS_UNPUBLISH, false);
		return participationItemRepository.findMatchingParticipationItem(criteria);
	}

	@Override
	public void updateParticipationItemStatus(
			int participationId,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus,
			Date processingDate
	) {
		// update the participation record
		participationItemRepository.updateParticipationItemStatus(
				participationId, status, updateStatus,
				participationEngineSettings.getTaskUserId(), processingDate);

		// add event for this update with a partial participation record for the details
		ParticipationItem eventItem = new ParticipationItem();
		eventItem.setId(participationId);
		eventItem.setLastModifiedUserId(participationEngineSettings.getTaskUserId());
		eventItem.setLastModifiedDate(processingDate);
		eventItem.setStatus(status);
		eventItem.setUpdateStatus(updateStatus);
		ContentEvent contentEvent = new ContentEvent();
		contentEvent.setParticipationItem(eventItem);
		contentEvent.setLastModifiedDate(processingDate);
		contentEvent.setLastModifiedUserId(participationEngineSettings.getTaskUserId());
		contentEventRepository.save(contentEvent);
	}

	/**
	 * A paginated result set would change since participations are marked as processed in between queries,
	 * so paged results will shift, possibly resulting in records being found twice or skipped over.
	 * To avoid this problem, query for a single participation at a time, process it, and repeat.
	 * Thus, pageSize MUST be 1.
	 */
	private ParticipationItemSearchCriteria createSearchCriteria(
			ParticipationItemUpdateStatus updateStatus,
			Boolean isExpired
	) {
		ParticipationItemSearchCriteria criteria = new ParticipationItemSearchCriteria();
		criteria.setStatuses(Collections.singleton(ParticipationItemStatus.PUBLISHED));
		criteria.setUpdateStatus(updateStatus);
		criteria.setIsExpired(isExpired);
		criteria.setPageSize(1);
		criteria.setPage(1);
		return criteria;
	}
}
