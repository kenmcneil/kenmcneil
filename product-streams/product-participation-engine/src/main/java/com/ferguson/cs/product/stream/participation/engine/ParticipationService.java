package com.ferguson.cs.product.stream.participation.engine;

public interface ParticipationService {

	/**
	 * Activate each participation that's pending activation.
	 */
	void processPendingActivations();

	/**
	 * Deactivate each participation that's pending deactivation.
	 */
	void processPendingDeactivations();

	/**
	 * Unpublish each participation that's pending unpublish.
	 */
	void processPendingUnpublishes();
}
