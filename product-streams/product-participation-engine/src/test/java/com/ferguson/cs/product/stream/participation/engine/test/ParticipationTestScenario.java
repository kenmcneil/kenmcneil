package com.ferguson.cs.product.stream.participation.engine.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioIngredient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class to use to build up test scenarios that test expected behavior at various points
 * of processing Participation records and their effects. Each scenario instance has a list
 * of mixin instances that will each be notified for state transition hooks such as "beforeActivation."
 *
 * Example:<pre>
 * testXYZ_abc() {
 *     Participation
 *     new ParticipationTestScenario()
 *          .fixtures(p1, p2, ...)
 *     //
 * }
 * </pre>
 */
@Service
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationTestScenario {
	private ParticipationTestUtilities participationTestUtilities;
	private ParticipationProcessor participationProcessor;

	@Builder.Default
	private boolean initialized = false;

	private Date originalDate;
	private Date currentDate;

	private List<ParticipationScenarioIngredient> ingredients;

	public ParticipationTestScenario onDay(Integer dayNumber) {
		Date futureDate = new Date(originalDate.getTime() + TimeUnit.DAYS.toMillis(dayNumber));
		Assertions.assertThat(futureDate).isAfterOrEqualsTo(currentDate);
		processEventsUpTo(futureDate);
		return this;
	}

	/**
	 * Process all events in each day, from the current date up to but not including the given future date.
	 * Use the start of the day of the current date plus 1 day so that all events scheduled
	 * for any time in that day are processed.
 	 */
	private void processEventsUpTo(Date futureDate) {
		// Use when(...) withPassthrough to get called when about to activate a participation,
		// then call each mixin's beforeActivation,
		// then activate, then call each mixin's afterActivation,
		// ...

		Date endOfDayDate = Date.from(
				LocalDate
						.from(currentDate.toInstant())
						.plusDays(1)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()
		);
		while (endOfDayDate.getTime() < futureDate.getTime()) {

			participationProcessor.process();

			// advance by a day
			endOfDayDate = new Date(endOfDayDate.getTime() + TimeUnit.DAYS.toMillis(1));
		}

		// We've incremented by days, but need to set current date to the exact future date given.
		currentDate = futureDate;
	}

	/**
	 * Publish given Participation records. The data for each Participation is represented
	 * by a ParticipationItemFixture object to make it easy to add test fixtures.
	 */
	public ParticipationTestScenario userPublishEvent(ParticipationItemFixture... fixtures) {
		Arrays.stream(fixtures).forEach(fixture -> participationTestUtilities.insertParticipation(fixture));
		return this;
	}

	/**
	 * Custom setter functions for the builder.
	 */
	public static class ParticipationTestScenarioBuilder {
		public ParticipationTestScenarioBuilder ingredients(ParticipationScenarioIngredient... params) {
			ingredients = Arrays.asList(params);
			return this;
		}

		public ParticipationTestScenarioBuilder runDate(Date date) {
			originalDate = date;
			currentDate = date;
			return this;
		}
	}
}
