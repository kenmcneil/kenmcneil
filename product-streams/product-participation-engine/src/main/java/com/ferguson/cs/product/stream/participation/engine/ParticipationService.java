package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

public interface ParticipationService {
	/**
	 * Determine if a participation is currently active.
	 */
	boolean getParticipationIsActive(Integer participationId);

	/**
	 * Activate a participation.
	 */
	void activateParticipation(ParticipationItem item, Date processingDate);

	/**
	 * Deactivate a participation.
	 */
	void deactivateParticipation(ParticipationItem item, Date processingDate);

	/**
	 * Unpublish a participation.
	 */
	void unpublishParticipation(ParticipationItem item, Date processingDate);
}
