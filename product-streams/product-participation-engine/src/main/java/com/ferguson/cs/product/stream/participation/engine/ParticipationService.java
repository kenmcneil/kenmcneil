package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

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
	 * Activate a participation.
	 */
	void activateParticipation(ParticipationItemPartial item, Date processingDate);

	/**
	 * Deactivate a participation.
	 */
	void deactivateParticipation(ParticipationItemPartial item, Date processingDate);

	/**
	 * Unpublish a participation.
	 */
	void unpublishParticipation(ParticipationItemPartial item, Date processingDate);
}
