package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

public interface ConstructService {

	/**
	 * Query for and return a participation that is pending publish. Return null if none found.
	 * Optionally restrict to records with id >= minParticipationId (for test mode).
	 * Populates only the participationId and lastModifiedUserId.
	 * This is a user-initiated event.
	 */
	ParticipationItem getNextPendingPublishParticipation(Integer minParticipationId);

	/**
	 * Query for and return a participation that is pending unpublish. Returns null if none found.
	 * Optionally restrict to records with id >= minParticipationId (for test mode).
	 * Populates only the participationId and lastModifiedUserId.
	 * This is a user-initiated event.
	 */
	ParticipationItem getNextPendingUnpublishParticipation(Integer minParticipationId);

	/**
	 * Update status fields and last-modified info in the given participation record,
	 * and add an event to record the update.
	 */
	void updateParticipationItemStatus(
			int participationId,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus,
			Date processingDate,
			int userId
	);
}
