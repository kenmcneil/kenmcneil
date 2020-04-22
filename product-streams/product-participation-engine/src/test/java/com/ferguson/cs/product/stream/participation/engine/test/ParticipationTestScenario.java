package com.ferguson.cs.product.stream.participation.engine.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationService;
import com.ferguson.cs.product.stream.participation.engine.ParticipationWriter;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
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
	// These must be mocked/injected in the test class that uses this class, and passed
	// to the constructor.
	private ConstructService constructService;
	private ParticipationService participationService;
	private ParticipationProcessor participationProcessor;
	private ParticipationWriter participationWriter;
	private ParticipationTestUtilities participationTestUtilities;

	private Date originalSimulatedDate;
	private Date currentSimulatedDate;

	private List<ParticipationScenarioIngredient> ingredients;
	private Queue<ParticipationItem> pendingUnpublishParticipationQueue = new LinkedList<>();

	public ParticipationTestScenario(
			ConstructService constructService,
			ParticipationService participationService,
			ParticipationProcessor participationProcessor,
			ParticipationWriter participationWriter,
			ParticipationTestUtilities participationTestUtilities
	) {
		this.constructService = constructService;
		this.participationService = participationService;
		this.participationProcessor = participationProcessor;
		this.participationWriter = participationWriter;
		this.participationTestUtilities = participationTestUtilities;
	}

	public void setupMocks() {
		// Replace polling the database with polling the scenarios's test queue.
		doAnswer(invocation -> pendingUnpublishParticipationQueue.poll())
				.when(constructService)
				.getNextPendingUnpublishParticipation();

		// Whenever a processing date is requested, return the simulated date.
		doAnswer(invocation -> currentSimulatedDate).when(participationProcessor).getProcessingDate();
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
		processEventsAtCurrentSimulatedDate();
		return this;
	}

	/**
	 * Process all events in each day, from the current date up to but not including the given future date.
	 * Use the start of the day of the current date plus 1 day so that all events scheduled
	 * for any time in that day are processed.
	 */
	private void processEventsUpTo(Date futureDate) {
		currentSimulatedDate = Date.from(
				LocalDate
						.from(currentSimulatedDate.toInstant())
						.plusDays(1)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant());
		while (currentSimulatedDate.getTime() < futureDate.getTime()) {
			processEventsAtCurrentSimulatedDate();

			// Advance by a day.
			currentSimulatedDate = new Date(currentSimulatedDate.getTime() + TimeUnit.DAYS.toMillis(1));
		}

		// Already incremented by day up to futureDate, now set current date to the exact future date given.
		currentSimulatedDate = futureDate;
	}

	/**
	 * Process all currently pending user events and process any time-based events on the given day.
	 */
	private void processEventsAtCurrentSimulatedDate() {
		// To Do
		// - Hook into methods called for various state transitions, in order to call Ingredient
		//   lifecycle methods (beforeActivation, ...), which will verify state.

		int unpublishQueueSize = pendingUnpublishParticipationQueue.size();

		participationProcessor.process();

		// Verify unpublish event queue is empty now.
		Assertions.assertThat(pendingUnpublishParticipationQueue.size()).isEqualTo(0);

		// Verify that mongo update status was called once for each event that was in the pendingUnpublishParticipationQueue.
		verify(constructService, times(unpublishQueueSize)).updateParticipationItemStatus(
				anyInt(), eq(ParticipationItemStatus.DRAFT), isNull(), any(Date.class));
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
			// Set start date to the beginning of the day.
			fixture.setStartDate(Date.from(
					LocalDate
							.from(currentSimulatedDate.toInstant())
							.plusDays(fixture.getStartDateOffsetDays())
							.atStartOfDay(ZoneId.systemDefault())
							.toInstant()));
		}

		if (fixture.getEndDateOffsetDays() != null) {
			// Set end date to the end of the day.
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
