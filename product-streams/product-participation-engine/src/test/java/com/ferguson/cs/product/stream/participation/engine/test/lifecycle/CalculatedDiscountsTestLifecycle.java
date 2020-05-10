package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

/**
 * Verify that calculated discounts effects e.g. price changes are correct.
 */
public class CalculatedDiscountsTestLifecycle extends ParticipationScenarioTestLifecycle {

	/**
	 *
	 */
	@Override
	public void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	/**
	 *
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());

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
