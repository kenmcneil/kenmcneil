package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.lang.Nullable;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

public class ParticipationReader {
	private final ConstructService constructService;

	public ParticipationReader(ConstructService constructService) {
		this.constructService = constructService;
	}

	/**
	 * Return the next participation pending unpublish, or null if there are none.
	 * This is a user-initiated event.
	 */
	@Nullable
	ParticipationItem getNextPendingUnpublishParticipation() {
		return constructService.getNextPendingUnpublishParticipation();
	}

	/**
	 * Return the next participation pending activation, or null if none found.
	 * This is a time-based event.
	 */
	@Nullable
	ParticipationItem getNextPendingActivationParticipation() {
		return constructService.getNextPendingActivationParticipation();
	}

	/**
	 * Return the next participation pending deactivation, or null if none found.
	 * This is a time-based event.
	 */
	@Nullable
	ParticipationItem getNextPendingDeactivationParticipation() {
		return constructService.getNextPendingDeactivationParticipation();
	}
}
