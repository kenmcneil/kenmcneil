package com.ferguson.cs.product.stream.participation.engine.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationService;
import com.ferguson.cs.product.stream.participation.engine.ParticipationWriter;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioIngredient;

/**
 * Use to build up test scenarios to test expected behavior at various points
 * of processing Participation records and their effects. Each scenario instance has a list
 * of "ingredient" instances that will each be notified for state transition hooks such
 * as "beforeActivation." Each ingredient is responsible for testing end-to-end behavior
 * of a specific feature such as a Participation effect.
 */
public class ParticipationTestScenario {
	@MockBean
	public ConstructService constructService;

	@MockBean
	public ParticipationService participationService;

	@MockBean
	public ParticipationWriter participationWriter;

	@InjectMocks
	public ParticipationProcessor participationProcessor;

	@Autowired
	public ParticipationDao participationDao;

	@Autowired
	private ParticipationTestUtilities participationTestUtilities;

	private Date originalSimulatedDate;
	private Date currentSimulatedDate;

	private List<ParticipationScenarioIngredient> ingredients;
	private Queue<ParticipationItem> pendingUnpublishParticipationQueue = new LinkedList<>();

	public ParticipationTestScenario() {
		// Replace polling the database with polling the scenarios's test queue.
		when(constructService.getNextPendingUnpublishParticipation()).thenAnswer(i -> pendingUnpublishParticipationQueue.poll());

		// When unpublishParticipation is called, replace the original date with the simulated date.
		doAnswer(invocation -> {
			participationService.unpublishParticipation(invocation.getArgument(0), currentSimulatedDate);
			return null;
		}).when(participationService).unpublishParticipation(any(ParticipationItem.class), any(Date.class));
	}

	public ParticipationTestScenario ingredients(ParticipationScenarioIngredient... params) {
		ingredients = Arrays.asList(params);
		return this;
	}

	public ParticipationTestScenario start(Date simulatedRunDate) {
		originalSimulatedDate = simulatedRunDate;
		currentSimulatedDate = simulatedRunDate;
		return this;
	}

	public ParticipationTestScenario advanceToDay(int dayNumber) {
		Date futureDate = new Date(originalSimulatedDate.getTime() + TimeUnit.DAYS.toMillis(dayNumber));
		Assertions.assertThat(futureDate).isAfterOrEqualsTo(currentSimulatedDate);
		processEventsUpTo(futureDate);
		return this;
	}

	/**
	 * Publish the given Participation record. The Participation record is represented
	 * by a ParticipationItemFixture object to make it easy to create test fixture data.
	 */
	public ParticipationTestScenario createUserPublishEvent(ParticipationItemFixture fixture) {
		simulatePublishEvent(fixture);
		return this;
	}

	/**
	 * Simulate a user unpublish event. Adds to list of records that will be returned by
	 * constructService.getNextPendingUnpublishParticipation.
	 */
	public ParticipationTestScenario createUserUnpublishEvent(ParticipationItemFixture fixture) {
		ParticipationItem p = new ParticipationItem();
		p.setId(fixture.getParticipationId());
		p.setLastModifiedUserId(fixture.getUserId());
		pendingUnpublishParticipationQueue.add(p);
		return this;
	}

	/**
	 * Process any pending events present at the current simulated date. This does not advance time.
	 * Use when either time advancing is not important, or events need to be processed again before
	 * advancing time.
	 */
	public ParticipationTestScenario processEvents() {
		processEventsOn(currentSimulatedDate);
		return this;
	}

	/**
	 * Process all events in each day, from the current date up to but not including the given future date.
	 * Use the start of the day of the current date plus 1 day so that all events scheduled
	 * for any time in that day are processed.
	 */
	private void processEventsUpTo(Date futureDate) {
		Date endOfDayDate = Date.from(
				LocalDate
						.from(currentSimulatedDate.toInstant())
						.plusDays(1)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()
		);
		while (endOfDayDate.getTime() < futureDate.getTime()) {
			processEventsOn(endOfDayDate);

			// advance by a day
			endOfDayDate = new Date(endOfDayDate.getTime() + TimeUnit.DAYS.toMillis(1));
		}

		// already incremented by day to futureDate, set current date to the exact future date given.
		currentSimulatedDate = futureDate;
	}

	/**
	 * Process all currently pending user events and process any time-based events on the given day.
	 */
	private void processEventsOn(Date simulatedDate) {
		// Use when(...) withPassthrough to get called when about to activate a participation,
		// then call each mixin's beforeActivation,
		// then activate, then call each mixin's afterActivation,
		// ...

		// set the simulated date

		participationProcessor.process();

	}

	/**
	 * Insert a participation record (same effect as a publish event). Since records are
	 * inserted to SQL currently as part of the user publish action, simply inserting the record
	 * here is all that's needed to simulate the publish event (i.e. no queue involved).
	 * Converts day offsets to actual dates if offsets were used.
	 */
	private void simulatePublishEvent(ParticipationItemFixture fixture) {
		// If day offsets were used, convert to actual dates based on the current run date.
		if (fixture.getStartDateOffsetDays() != null) {
			// set start date to the beginning of the day
			fixture.setStartDate(Date.from(
					LocalDate
							.from(currentSimulatedDate.toInstant())
							.plusDays(fixture.getStartDateOffsetDays())
							.atStartOfDay(ZoneId.systemDefault())
							.toInstant()));
		}

		if (fixture.getEndDateOffsetDays() != null) {
			// set end date to the end of the day
			fixture.setEndDate(Date.from(
					LocalDate
							.from(currentSimulatedDate.toInstant())
							.plusDays(fixture.getEndDateOffsetDays() + 1)
							.atStartOfDay(ZoneId.systemDefault())
							.minus(1, ChronoUnit.MINUTES)
							.toInstant()));
		}

		participationTestUtilities.insertParticipation(fixture);
	}
}
