package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;

/**
 * Lifecycle tests should extend this class rather than implementing ParticipationScenarioLifecycleTest,
 * so dependencies are autowired.
 */
@Component
public abstract class BaseParticipationScenarioLifecycleTest implements ParticipationScenarioLifecycleTest {
	@Autowired
	protected ParticipationTestUtilities participationTestUtilities;

	public abstract void beforePublish(ParticipationItemFixture fixture, Date processingDate);

	public abstract void afterPublish(ParticipationItemFixture fixture, Date processingDate);

	public abstract void beforeActivate(ParticipationItemFixture fixture, Date processingDate);

	public abstract void afterActivate(ParticipationItemFixture fixture, Date processingDate);

	public abstract void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate);

	public abstract void afterDeactivate(ParticipationItemFixture fixture, Date processingDate);

	public abstract void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate);

	public abstract void afterUnpublish(ParticipationItemFixture fixture, Date processingDate);
}
