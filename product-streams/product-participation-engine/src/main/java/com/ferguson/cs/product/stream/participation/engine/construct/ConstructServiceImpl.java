package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSearchCriteria;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

// TODO make a query to return only one item, use it in this class
@Service
public class ConstructServiceImpl implements ConstructService {

	@Autowired
	private ParticipationItemRepository participationItemRepository;

	@Override
	public ParticipationItem getNextPendingActivationParticipation() {
		ParticipationItemSearchCriteria criteria = createSearchCriteria(
				ParticipationItemUpdateStatus.NEEDS_UPDATE, false);
		criteria.setScheduledOn(new Date());
		List<ParticipationItem> items = participationItemRepository.findMatchingParticipationItems(criteria).getData();
		return items.size() > 0 ? items.get(0) : null;
	}

	@Override
	public ParticipationItem getNextPendingDeactivationParticipation() {
		ParticipationItemSearchCriteria criteria = createSearchCriteria(null, true);
		List<ParticipationItem> items = participationItemRepository.findMatchingParticipationItems(criteria).getData();
		return items.size() > 0 ? items.get(0) : null;
	}

	@Override
	public ParticipationItem getNextPendingUnpublishParticipation() {
		ParticipationItemSearchCriteria criteria = createSearchCriteria(ParticipationItemUpdateStatus.NEEDS_UNPUBLISH, false);
		List<ParticipationItem> items = participationItemRepository.findMatchingParticipationItems(criteria).getData();
		return items.size() > 0 ? items.get(0) : null;
	}

	/**
	 * A paginated result set would change since participations are marked as processed in between queries,
	 * so paged results will shift, possibly resulting in records being found twice or skipped over.
	 * To avoid this problem, query for a single participation at a time, process it, and repeat.
	 * Thus, pageSize MUST be 1.
	 * @param updateStatus
	 * @param isExpired
	 * @return
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
