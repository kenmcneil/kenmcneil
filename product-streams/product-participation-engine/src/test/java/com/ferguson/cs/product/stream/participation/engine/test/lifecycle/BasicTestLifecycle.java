package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

/**
 * Verify that the basic publish, activate, deactivate, and unpublish transitions work.
 * Check for existence of references to the participation, and verify isActive state.
 */
public class BasicTestLifecycle extends ParticipationScenarioTestLifecycle {

	/**
	 * Verify there are no references to the participation yet.
	 */
	@Override
	public void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
		participationTestUtilities.assertParticipationNotPresent(fixture);
	}

	/**
	 * Verify that there are references to the participation now.
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial).isNotNull();
		Assertions.assertThat(itemPartial.getIsActive()).isFalse();
		if (fixture.getStartDate() == null || itemPartial.getStartDate() == null) {
			Assertions.assertThat(itemPartial.getStartDate()).isEqualTo(fixture.getStartDate());
		} else {
			Assertions.assertThat(itemPartial.getStartDate().getTime()).isEqualTo(fixture.getStartDate().getTime());
		}
		if (fixture.getEndDate() == null || itemPartial.getEndDate() == null) {
			Assertions.assertThat(itemPartial.getEndDate()).isEqualTo(fixture.getEndDate());
		} else {
			Assertions.assertThat(itemPartial.getEndDate().getTime()).isEqualTo(fixture.getEndDate().getTime());
		}
		Assertions.assertThat(itemPartial.getLastModifiedUserId()).isEqualTo(fixture.getLastModifiedUserId());
	}

	/**
	 * Verify the participation is present but not active yet.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial).isNotNull();
		Assertions.assertThat(itemPartial.getIsActive()).isFalse();
	}

	/**
	 * Verify the participation is present and has been activated.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial).isNotNull();
		Assertions.assertThat(itemPartial.getIsActive()).isTrue();
	}

	/**
	 * Verify the participation is present and active.
	 */
	@Override
	public void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial).isNotNull();
		Assertions.assertThat(itemPartial.getIsActive()).isTrue();
	}

	/**
	 * Verify the participation is present and no longer active.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial).isNotNull();
		Assertions.assertThat(itemPartial.getIsActive()).isFalse();
	}

	/**
	 * Verify that the participation is still present but inactive.
	 */
	@Override
	public void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial).isNotNull();
		Assertions.assertThat(itemPartial.getIsActive()).isFalse();
	}

	/**
	 * Verify there are no references to the participation anymore.
	 */
	@Override
	public void afterUnpublish(ParticipationItemFixture fixture, Date processingDate) {
		participationTestUtilities.assertParticipationNotPresent(fixture);
	}
}
