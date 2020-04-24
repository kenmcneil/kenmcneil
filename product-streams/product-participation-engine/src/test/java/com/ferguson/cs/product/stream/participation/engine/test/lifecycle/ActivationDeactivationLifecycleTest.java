package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioLifecycleTest;

/**
 * Verify that the basic publish, activate, deactivate, unpublish transitions work.
 * Checks for existence of references to the participation, and isActive state.
 */
public class ActivationDeactivationLifecycleTest implements ParticipationScenarioLifecycleTest {
	private ParticipationTestUtilities participationTestUtilities;

	public void init(ParticipationTestUtilities participationTestUtilities) {
		this.participationTestUtilities = participationTestUtilities;
	}

	/**
	 * Verify there are no references to the participation yet.
	 */
	@Override
	public void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
		Assertions.assertThat(participationTestUtilities.isParticipationPresent(fixture.getParticipationId())).isFalse();
	}

	/**
	 * Verify that there are references to the participation now.
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture item = participationTestUtilities.getParticipationAsFixtureById(fixture.getParticipationId());
		Assertions.assertThat(item).isNotNull();
		Assertions.assertThat(item.getIsActive()).isFalse();
	}

	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture item = participationTestUtilities.getParticipationAsFixtureById(fixture.getParticipationId());
		Assertions.assertThat(item).isNotNull();
		Assertions.assertThat(item.getIsActive()).isFalse();
	}

	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture item = participationTestUtilities.getParticipationAsFixtureById(fixture.getParticipationId());
		Assertions.assertThat(item).isNotNull();
		Assertions.assertThat(item.getIsActive()).isTrue();
	}

	@Override
	public void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture item = participationTestUtilities.getParticipationAsFixtureById(fixture.getParticipationId());
		Assertions.assertThat(item).isNotNull();
		Assertions.assertThat(item.getIsActive()).isTrue();
	}

	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture item = participationTestUtilities.getParticipationAsFixtureById(fixture.getParticipationId());
		Assertions.assertThat(item).isNotNull();
		Assertions.assertThat(item.getIsActive()).isFalse();
	}

	@Override
	public void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture item = participationTestUtilities.getParticipationAsFixtureById(fixture.getParticipationId());
		Assertions.assertThat(item).isNotNull();
		Assertions.assertThat(item.getIsActive()).isFalse();
	}

	@Override
	public void afterUnpublish(ParticipationItemFixture fixture, Date processingDate) {
		Assertions.assertThat(participationTestUtilities.isParticipationPresent(fixture.getParticipationId())).isFalse();
	}
}
