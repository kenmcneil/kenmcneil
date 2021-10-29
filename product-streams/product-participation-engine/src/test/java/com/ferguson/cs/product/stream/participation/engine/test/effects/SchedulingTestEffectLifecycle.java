package com.ferguson.cs.product.stream.participation.engine.test.effects;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

import lombok.RequiredArgsConstructor;

/**
 * Verify that scheduling works - it should be activated at the start date and deactivated
 * on the end date.
 *
 * These tests are not specific to the Participation type.
 */
@RequiredArgsConstructor
public class SchedulingTestEffectLifecycle implements ParticipationTestEffectLifecycle {
	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify not activating before the start date, and not activating if after the end date.
	 */
	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());

		// If there's a non-null start date, then should not be activating if the start date is not before
		// the processing date; null means activate as soon as possible, so no need to check in that case.
		if (itemPartial.getStartDate() != null) {
			Assertions.assertThat(itemPartial.getStartDate().getTime()).isLessThanOrEqualTo(processingDate.getTime());
		}

		// Should not activate if the end date is non-null and before the current processing date. The
		// processing date could be after the end date without ever activating the Participation if the
		// engine has not been run since the start date.
		if (itemPartial.getEndDate() != null) {
			Assertions.assertThat(itemPartial.getEndDate().getTime()).isGreaterThan(processingDate.getTime());
		}
	}

	/**
	 * Verify activated near the start date.
	 */
	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());

		if (itemPartial.getStartDate() != null) {
			// Should have activated near the start date. Verify no more than one day has passed (since
			// that's the resolution of the scenario test system if using day offsets).
			LocalDate startDatePlusOneDay = LocalDate.from(
					itemPartial.getStartDate().toInstant().atZone(ZoneId.of("UTC"))).plusDays(1);
			Assertions.assertThat(LocalDate.from(processingDate.toInstant().atZone(ZoneId.of("UTC")))).isBeforeOrEqualTo(startDatePlusOneDay);
		}
	}

	/**
	 * Verify not deactivating before the end date.
	 */
	@Override
	public void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());

		if (itemPartial.getStartDate() != null) {
			// Should not be deactivating if the start date is not before the processing date.
			Assertions.assertThat(itemPartial.getStartDate().getTime()).isLessThanOrEqualTo(processingDate.getTime());
		}
	}

	/**
	 * Verify deactivated near the end date.
	 */
	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());

		if (itemPartial.getEndDate() != null) {
			// Should have deactivated near the end date. Verify no more than one day has passed (since
			// that's the resolution of the scenario test system if using day offsets).
			LocalDate endDatePlusOneDay = LocalDate.from(itemPartial.getEndDate().toInstant().atZone(ZoneId.of("UTC"))).plusDays(1);
			Assertions.assertThat(LocalDate.from(processingDate.toInstant().atZone(ZoneId.of("UTC")))).isBeforeOrEqualTo(endDatePlusOneDay);
		}
	}
}
