package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;

/**
 * Provides ability to test a specific aspect of a Participation through its lifecycle.
 * The beforePublish and afterPublish events are simulated since publish is currently handled
 * in core-services when the user publishes their Participation.
 */
public interface ParticipationScenarioLifecycleTest {
	void beforePublish(ParticipationItemFixture fixture, Date processingDate);

	void afterPublish(ParticipationItemFixture fixture, Date processingDate);

	void beforeActivate(ParticipationItemFixture fixture, Date processingDate);

	void afterActivate(ParticipationItemFixture fixture, Date processingDate);

	void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate);

	void afterDeactivate(ParticipationItemFixture fixture, Date processingDate);

	void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate);

	void afterUnpublish(ParticipationItemFixture fixture, Date processingDate);
}
