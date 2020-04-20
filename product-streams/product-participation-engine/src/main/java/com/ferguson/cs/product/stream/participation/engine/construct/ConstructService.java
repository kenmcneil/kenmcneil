package com.ferguson.cs.product.stream.participation.engine.construct;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;

public interface ConstructService {

	/**
	 * Query for and return a participation that is pending unpublish. Return null if none found.
	 * This is a user-initiated event.
	 */
	ParticipationItem getNextPendingUnpublishParticipation();

	/**
	 * Query for and return a participation that is pending activation. Return null if none found.
	 * This is a time-based event.
	 */
	ParticipationItem getNextPendingActivationParticipation();

	/**
	 * Query for and return a participation that is pending deactivation. Return null if none found.
	 * This is a time-based event.
	 */
	ParticipationItem getNextPendingDeactivationParticipation();

	/**
	 * Update status fields and last-modified info in the given participation record,
	 * and add an event to record the update. Records the change as being made by the
	 * headless user that is configured in participation-engine.task-user-id.
	 */
	void updateParticipationItemStatus(
			int participationId,
			ParticipationItemStatus status,
			ParticipationItemUpdateStatus updateStatus,
			Date processingDate
	);
}
