package com.ferguson.cs.product.stream.participation.engine.test.effects;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

/**
 * All scenario effect lifecycle test classes must implement this interface. Each before* and
 * after* method is defaulted in case implementer doesn't need it. For type-specific rather
 * than effect-specific areas see ParticipationTestLifecycle.
 */
public interface ParticipationTestEffectLifecycle {
	default void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterUnpublish(ParticipationItemFixture fixture, Date processingDate) {
	}
}
