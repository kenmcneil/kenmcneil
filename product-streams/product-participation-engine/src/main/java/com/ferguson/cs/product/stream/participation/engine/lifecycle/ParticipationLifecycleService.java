package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

/**
 * Given a ParticipationItem, calls the correct lifecycle strategy bean based on the
 * content type, which is the schema name and version of the content type definition
 * used to create the record's content.
 *
 * Be sure to add new implementing classes to the ParticipationLifecycleService
 * constructor in ParticipationEngineConfiguration.
 */
public interface ParticipationLifecycleService {
	/**
	 * Get next participation that is pending activation at the given date.
	 */
	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate);

	/**
	 * Get next participation that is pending deactivation at the given date.
	 */
	ParticipationItemPartial getNextExpiredParticipation(Date processingDate);

	/**
	 * Call the publish lifecycle method for the type of the given Participation.
	 */
	int publishByType(ParticipationItem item, Date processingDate);

	/**
	 * Call the activate lifecycle method for the type of the given Participation.
	 */
	int activateByType(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Call the deactivate lifecycle method for the type of the given Participation.
	 */
	int deactivateByType(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Call the lifecycle unpublish method for the type of the given Participation.
	 */
	int unpublishByType(ParticipationItemPartial itemPartial, Date processingDate);

	/**
	 * Determine if a participation is currently active.
	 */
	Boolean getParticipationIsActive(Integer participationId);

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	int syncToCurrentPriorityParticipation();
}
