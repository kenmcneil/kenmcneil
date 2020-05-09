package com.ferguson.cs.product.stream.participation.engine.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

/**
 * All scenario lifecycle test classes should extend this class.
 * The before* and after* methods are concrete so that subclasses need not implement them.
 */
@Component
public abstract class ParticipationScenarioTestLifecycle {
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
