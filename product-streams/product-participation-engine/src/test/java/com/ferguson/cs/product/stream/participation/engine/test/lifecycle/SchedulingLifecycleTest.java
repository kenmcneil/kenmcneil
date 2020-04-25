package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioLifecycleTest;

/**
 * Verify that scheduling works - it should be activated at the start date and deactivated
 * on the end date.
 */
public class SchedulingLifecycleTest implements ParticipationScenarioLifecycleTest {
	private ParticipationTestUtilities participationTestUtilities;

	public void init(ParticipationTestUtilities participationTestUtilities) {
		this.participationTestUtilities = participationTestUtilities;
	}

	@Override
	public void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
		// no checks
	}

	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		// no checks
	}

	@Override
	public void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture fixtureFromDatabase = participationTestUtilities.getParticipationItemPartialAsFixture(fixture.getParticipationId());

		// Should not be activating if the start date is not before the processing date.
		Assertions.assertThat(fixtureFromDatabase.getStartDate().getTime()).isLessThanOrEqualTo(processingDate.getTime());

		// Should not activate if the end date is before the current processing date. The processing date
		// could be after the end date without ever activating the Participation if the engine has not
		// been run since the start date.
		Assertions.assertThat(fixtureFromDatabase.getEndDate().getTime()).isGreaterThan(processingDate.getTime());
	}

	@Override
	public void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture fixtureFromDatabase = participationTestUtilities.getParticipationItemPartialAsFixture(fixture.getParticipationId());

		// Should have activated near the start date. Verify no more than one day has passed (since
		// that's the resolution of the scenario test system if using day offsets).
		LocalDate startDatePlusOneDay = LocalDate.from(fixtureFromDatabase.getStartDate().toInstant().atZone(ZoneId.systemDefault())).plusDays(1);
		Assertions.assertThat(LocalDate.from(processingDate.toInstant().atZone(ZoneId.systemDefault()))).isBeforeOrEqualTo(startDatePlusOneDay);
	}

	@Override
	public void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture fixtureFromDatabase = participationTestUtilities.getParticipationItemPartialAsFixture(fixture.getParticipationId());

		// Should not be deactivating if the start date is not before the processing date.
		Assertions.assertThat(fixtureFromDatabase.getStartDate().getTime()).isLessThanOrEqualTo(processingDate.getTime());
	}

	@Override
	public void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemFixture fixtureFromDatabase = participationTestUtilities.getParticipationItemPartialAsFixture(fixture.getParticipationId());
		Assertions.assertThat(fixtureFromDatabase).isNotNull();
		Assertions.assertThat(fixtureFromDatabase.getIsActive()).isFalse();

		// Should have deactivated near the end date. Verify no more than one day has passed (since
		// that's the resolution of the scenario test system if using day offsets).
		LocalDate endDatePlusOneDay = LocalDate.from(fixtureFromDatabase.getEndDate().toInstant().atZone(ZoneId.systemDefault())).plusDays(1);
		Assertions.assertThat(LocalDate.from(processingDate.toInstant().atZone(ZoneId.systemDefault()))).isBeforeOrEqualTo(endDatePlusOneDay);
	}

	@Override
	public void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate) {
		// no checks
	}

	@Override
	public void afterUnpublish(ParticipationItemFixture fixture, Date processingDate) {
		// no checks
	}
}
