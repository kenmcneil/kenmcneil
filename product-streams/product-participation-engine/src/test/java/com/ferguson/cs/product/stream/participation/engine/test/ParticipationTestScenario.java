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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationService;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioLifecycleTest;

/**
 * Use to build up test scenarios to test expected behavior at various points
 * of processing Participation records and their effects. Each scenario instance has a list
 * of lifecycle test instances that will each be notified for state transition hooks such
 * as beforeActivation. Each test is responsible for testing end-to-end behavior
 * of a specific feature such as a Participation effect.
 */
public class ParticipationTestScenario {
	// These must be mocked/injected in the test class that uses this class, and passed
	// to the constructor.
	private ParticipationEngineSettings participationEngineSettings;
	private ConstructService constructService;
	private ParticipationService participationService;
	private ParticipationProcessor participationProcessor;
	private ParticipationTestUtilities participationTestUtilities;

	private Date originalSimulatedDate;
	private Date currentSimulatedDate;

	private List<ParticipationScenarioLifecycleTest> lifecycleTests;
	private Map<Integer, ParticipationItemFixture> fixtures = new HashMap<>();
	private Queue<ParticipationItem> pendingUnpublishParticipationQueue = new LinkedList<>();

	public ParticipationTestScenario(
			ParticipationEngineSettings participationEngineSettings,
			ConstructService constructService,
			ParticipationService participationService,
			ParticipationProcessor participationProcessor,
			ParticipationTestUtilities participationTestUtilities
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.constructService = constructService;
		this.participationService = participationService;
		this.participationProcessor = participationProcessor;
		this.participationTestUtilities = participationTestUtilities;

		originalSimulatedDate = new Date();
		currentSimulatedDate = originalSimulatedDate;
	}

	/**
	 * Set up a variety of mocks and spies to enable simulating time and user events. Also
	 * doAnswer is used to enable AOP-like behavior to call tests before and after
	 * listeners.
	 */
	public void setupMocks() {
		Integer minParticipationId = participationEngineSettings.getTestModeMinParticipationId();

		// Replace polling the database with polling the scenarios's test queue.
		doAnswer(invocation -> pendingUnpublishParticipationQueue.poll())
				.when(constructService)
				.getNextPendingUnpublishParticipation(minParticipationId);

		// Whenever a processing date is requested, return the simulated date.
		doAnswer(invocation -> currentSimulatedDate).when(participationProcessor).getProcessingDate();

		// Set up before and after calls for when an ACTIVATE event is processed.
		doAnswer(invocation -> {
			beforeActivate(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterActivate(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationService).activateParticipation(any(ParticipationItem.class), any(Date.class));

		// Set up before and after calls for when an DEACTIVATE event is processed.
		doAnswer(invocation -> {
			beforeDeactivate(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterDeactivate(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationService).deactivateParticipation(any(ParticipationItem.class), any(Date.class));

		// Set up before and after calls for when an UNPUBLISH event is processed.
		doAnswer(invocation -> {
			beforeUnpublish(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterUnpublish(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationService).unpublishParticipation(any(ParticipationItem.class), any(Date.class));
	}

	public ParticipationTestScenario lifecyleTests(ParticipationScenarioLifecycleTest... params) {
		lifecycleTests = Arrays.asList(params);
		lifecycleTests.forEach(test -> test.init(participationTestUtilities));
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
	 * Keep track of fixtures used so they may be accessed by lifecycle tests for verification.
	 * Also ensure critical values are set or defaulted, e.g. participationId and lastModifiedUserId.
	 */
	public void initAndRememberFixture(ParticipationItemFixture fixture) {
		Assertions.assertThat(fixture.getParticipationId()).isNotNull();
		if (fixture.getLastModifiedUserId() == null) {
			fixture.setLastModifiedUserId(ParticipationTestUtilities.TEST_USERID);
		}
		fixtures.putIfAbsent(fixture.getParticipationId(), fixture);
	}

	/**
	 * Publish the given Participation record. The Participation record is represented
	 * by a ParticipationItemFixture object to make it easy to create test fixture data.
	 */
	public ParticipationTestScenario createUserPublishEvent(ParticipationItemFixture fixture) {
		initAndRememberFixture(fixture);
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
		p.setLastModifiedUserId(fixture.getLastModifiedUserId());
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
		int unpublishQueueSize = pendingUnpublishParticipationQueue.size();

		participationProcessor.process();

		// Verify unpublish event queue is empty now and that mongo update status was called
		// once for each event that was in the queue.
		Assertions.assertThat(pendingUnpublishParticipationQueue.size()).isEqualTo(0);
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

		beforePublish(fixture, currentSimulatedDate);
		participationTestUtilities.insertParticipation(fixture);
		afterPublish(fixture, currentSimulatedDate);
	}

	private void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforePublish(fixture, processingDate));
	}

	private void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.afterPublish(fixture, processingDate));
	}

	private void beforeActivate(ParticipationItem fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeActivate(fixtures.get(fixture.getId()), processingDate));
	}

	private void afterActivate(ParticipationItem fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.afterActivate(fixtures.get(fixture.getId()), processingDate));
	}

	private void beforeDeactivate(ParticipationItem fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeDeactivate(fixtures.get(fixture.getId()), processingDate));
	}

	private void afterDeactivate(ParticipationItem fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.afterDeactivate(fixtures.get(fixture.getId()), processingDate));
	}

	private void beforeUnpublish(ParticipationItem fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeUnpublish(fixtures.get(fixture.getId()), processingDate));
	}

	private void afterUnpublish(ParticipationItem fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.afterUnpublish(fixtures.get(fixture.getId()), processingDate));
	}
}
