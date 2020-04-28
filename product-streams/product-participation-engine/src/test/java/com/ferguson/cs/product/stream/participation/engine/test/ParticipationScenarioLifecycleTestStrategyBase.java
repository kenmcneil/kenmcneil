package com.ferguson.cs.product.stream.participation.engine.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioLifecycleTestStrategy;

/**
 * Lifecycle tests should extend this class rather than implementing ParticipationScenarioLifecycleTestStrategy,
 * so dependencies are autowired.
 *
 * The before* and after* methods are concrete so that subclasses need not implement them.
 */
@Component
public abstract class ParticipationScenarioLifecycleTestStrategyBase implements ParticipationScenarioLifecycleTestStrategy {
	@Autowired
	protected ParticipationTestUtilities participationTestUtilities;

	public void beforePublish(ParticipationItemFixture fixture, Date processingDate) {}

	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {}

	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {}

	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {}

	public void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {}

	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {}

	public void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate) {}

	public void afterUnpublish(ParticipationItemFixture fixture, Date processingDate) {}
}
