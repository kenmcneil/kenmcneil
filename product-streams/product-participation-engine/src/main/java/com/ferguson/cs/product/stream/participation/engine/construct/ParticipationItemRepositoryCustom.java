package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSearchCriteria;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

public interface ParticipationItemRepositoryCustom {
	/**
	 * Update status fields and last-modified info in the given participation record.
	 * Only updates status if given status is not PUBLISHED (since it's already published).
	 * Sets updateStatus to given value, even if null.
	 */
	void updateParticipationItemStatus(
			int participationId,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus,
			int userId,
			Date processingDate
	);

	/**
	 * Query the participation item collection and return the first result found.
	 * Populates only id and lastModifiedUserId properties.
	 * @param criteria {@link ParticipationItemSearchCriteria} The criteria by which to filter results.
	 * @return A {@link ParticipationItem} or null if none found.
	 */
	ParticipationItem findParticipationItemEvent(ParticipationItemSearchCriteria criteria);
}