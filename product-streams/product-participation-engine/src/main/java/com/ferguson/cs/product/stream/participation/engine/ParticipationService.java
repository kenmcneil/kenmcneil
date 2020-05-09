package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

public interface ParticipationService {

	/**
	 * Get next participation that is pending activation at the given date.
	 */
	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate);

	/**
	 * Get next participation that is pending deactivation at the given date.
	 */
	ParticipationItemPartial getNextExpiredParticipation(Date processingDate);

	/**
	 * Determine if a participation is currently active.
	 */
	Boolean getParticipationIsActive(Integer participationId);

	/**
	 * Publish a participation record by upserting its data in to SQL. Expects a fully-populated
	 * ParticipationItem record from Construct, so all the data to insert is present.
	 */
	void publishParticipation(ParticipationItem item, Date processingDate);

	/**
	 * Activate a participation.
	 */
	void activateParticipation(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Deactivate a participation.
	 */
	void deactivateParticipation(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Unpublish a participation.
	 */
	void unpublishParticipation(ParticipationItemPartial itemPartial, Date processingDate);

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	int syncToCurrentPriorityParticipation();
}
