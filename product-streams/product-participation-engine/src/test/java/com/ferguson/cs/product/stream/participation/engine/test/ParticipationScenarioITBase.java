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
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationService;
import com.ferguson.cs.product.stream.participation.engine.ParticipationWriter;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.BasicLifecycleTestStrategy;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.SaleIdEffectLifecycleTestStrategy;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.SchedulingLifecycleTestStrategy;
import com.ferguson.cs.product.stream.participation.engine.test.model.LifecycleState;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationProduct;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioLifecycleTestStrategy;

/**
 * Subclass this to create scenarios that test expected behavior at various points
 * of processing Participation records and their effects. This class maintains state to make
 * writing scenarios easier by handling mocking and spying automatically. Each test has a list
 * of lifecycle tests that will each be notified for state transition hooks such
 * as beforeActivation. Each lifecycle test is responsible for testing end-to-end behavior
 * of a specific feature such as a Participation effect.
 */
@Import(ParticipationScenarioITBase.BaseParticipationScenarioITConfiguration.class)
public abstract class ParticipationScenarioITBase extends ParticipationEngineITBase {

	@TestConfiguration
	public static class BaseParticipationScenarioITConfiguration {
		@Bean
		public BasicLifecycleTestStrategy activationDeactivationLifecycleTest() {
			return new BasicLifecycleTestStrategy();
		}

		@Bean
		public SchedulingLifecycleTestStrategy schedulingLifecycleTest() {
			return new SchedulingLifecycleTestStrategy();
		}

		@Bean
		public SaleIdEffectLifecycleTestStrategy saleIdEffectLifecycleTest() {
			return new SaleIdEffectLifecycleTestStrategy();
		}
	}

	/*
	 * No mocking/spying needed:
	 *      ParticipationDao
	 *
	 * Mock entire class:
	 *      ConstructService
	 *
	 * Spy on class to override specific methods or do things before/after a method is called:
	 *      participationWriter: after processActivation, processDeactivation, processUnpublish
	 *      participationService: before and after processActivation, processDeactivation, processUnpublish
	 *      participationProcessor: getProcessingDate
	 */
	@Autowired
	protected ParticipationDao participationDao;

	@Autowired
	protected ParticipationEngineSettings participationEngineSettings;

	@MockBean
	protected ConstructService constructService;

	@SpyBean
	protected ParticipationService participationService;

	@SpyBean
	protected ParticipationWriter participationWriter;

	@SpyBean
	protected ParticipationProcessor participationProcessor;

	@Autowired
	protected BasicLifecycleTestStrategy basicLifecycleTestStrategy;

	@Autowired
	protected SchedulingLifecycleTestStrategy schedulingLifecycleTestStrategy;

	@Autowired
	protected SaleIdEffectLifecycleTestStrategy saleIdEffectLifecycleTestStrategy;

	// Properties to track Scenario test state.
	protected Date originalSimulatedDate;
	protected Date currentSimulatedDate;
	protected List<ParticipationScenarioLifecycleTestStrategy> lifecycleTests;
	protected Map<Integer, ParticipationItemFixture> fixtures = new HashMap<>();
	protected Queue<ParticipationItem> pendingUnpublishParticipationQueue = new LinkedList<>();

	private int nextTestParticipationId;

	private boolean ranBeforeAll = false;

	@Before
	public void before() {
		super.before();

		// Perform before-all initialization. Where are you junit 5?
		if (!ranBeforeAll) {
			setupMocks();
			ranBeforeAll = true;
		}

		// Default the simulated scenario start date.
		originalSimulatedDate = new Date();
		currentSimulatedDate = originalSimulatedDate;

		// Start the participation ids used for text fixtures at the test-mode min id.
		nextTestParticipationId = participationEngineSettings.getTestModeMinParticipationId();
	}

	public ParticipationScenarioITBase useTestStrategies(ParticipationScenarioLifecycleTestStrategy... params) {
		lifecycleTests = Arrays.asList(params);
		return this;
	}

	public ParticipationScenarioITBase startOn(Date simulatedRunDate) {
		originalSimulatedDate = simulatedRunDate;
		currentSimulatedDate = simulatedRunDate;
		return this;
	}

	public ParticipationScenarioITBase advanceToDay(int dayNumber) {
		Date futureDate = new Date(originalSimulatedDate.getTime() + TimeUnit.DAYS.toMillis(dayNumber));
		Assertions.assertThat(futureDate).isAfterOrEqualsTo(currentSimulatedDate);
		processEventsUpTo(futureDate);
		return this;
	}

	/**
	 * Returns a new test participation fixture id.
	 */
	private int getNextTestParticipationId() {
		return nextTestParticipationId++;
	}

	/**
	 * Keep track of fixtures used so they may be accessed by lifecycle tests for verification.
	 * Also ensure critical values are set or defaulted, e.g. participationId and lastModifiedUserId.
	 */
	public void initAndRememberFixture(ParticipationItemFixture fixture) {
		if (fixture.getParticipationId() == null) {
			fixture.setParticipationId(getNextTestParticipationId());
		}

		Assertions.assertThat(fixture.getParticipationId()).isGreaterThanOrEqualTo(
				participationEngineSettings.getTestModeMinParticipationId());

		if (fixture.getLastModifiedUserId() == null) {
			fixture.setLastModifiedUserId(ParticipationTestUtilities.TEST_USERID);
		}

		fixtures.putIfAbsent(fixture.getParticipationId(), fixture);
	}

	/**
	 * Publish the given Participation record. The Participation record is represented
	 * by a ParticipationItemFixture object to make it easy to create test fixture data.
	 */
	public ParticipationScenarioITBase createUserPublishEvent(ParticipationItemFixture fixture) {
		initAndRememberFixture(fixture);
		simulatePublishEvent(fixture);
		return this;
	}

	/**
	 * Simulate a user unpublish event. Adds to list of records that will be returned by
	 * constructService.getNextPendingUnpublishParticipation.
	 */
	public ParticipationScenarioITBase createUserUnpublishEvent(ParticipationItemFixture fixture) {
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
	public ParticipationScenarioITBase processEvents() {
		processEventsAtCurrentSimulatedDate();
		return this;
	}

	/*
	 * Lifecycle state transitions
	 *
	 * A list of state changes that must have occurred for the ingredient to report success.
	 *
	 *  - Possible states: published, activated, deactivated, unpublished
	 *  - All allowed transitions:
	 *      Empty state -> PUBLISHED
	 *      PUBLISHED -> (PUBLISHED, ACTIVATED, UNPUBLISHED)
	 *      ACTIVATED -> DEACTIVATED
	 *      DEACTIVATED -> UNPUBLISHED
	 *      UNPUBLISHED -> Deleted in SQL
	 */

	/**
	 * Verify that the given participation(s) experienced all the basic lifecycle states,
	 * in order from start to finish, with no extra states. Extra states could happen
	 * when the user performs actions such as unpublish and then publish. Another example
	 * is when an author publishes P, then Publish-Changes P, then it activates, which
	 * would result in: PUBLISHED, PUBLISHED, ACTIVATED, DEACTIVATED, UNPUBLISHED.
	 */
	public void verifySimpleLifecycleLog(ParticipationItemFixture... fixtures) {
		Arrays.stream(fixtures).forEach(fixture ->
				Assertions.assertThat(fixture.getStateLog())
					.as(fixture.toString())
					.containsExactly(
					LifecycleState.PUBLISHED,
					LifecycleState.ACTIVATED,
					LifecycleState.DEACTIVATED,
					LifecycleState.UNPUBLISHED
			)
		);
	}

	/**
	 * Verify that the given participation experienced all the basic lifecycle states,
	 * in order from start to finish, with no extra states
	 */
	public void verifyLifecycleLogMatches(ParticipationItemFixture p, LifecycleState... states) {
		Assertions.assertThat(p.getStateLog()).containsExactly(states);
	}

	public void verifyParticipationOwnsExactly(ParticipationItemFixture fixture, Integer... expectedUniqueIds) {
		List<Integer> ownedUniqueIds = participationTestUtilities
				.getParticipationProducts(fixture.getParticipationId()).stream()
				.filter(ParticipationProduct::getIsOwner)
				.map(ParticipationProduct::getUniqueId)
				.collect(Collectors.toList());
		Assertions.assertThat(ownedUniqueIds).containsExactlyInAnyOrderElementsOf(Arrays.asList(expectedUniqueIds));
	}

	/**
	 * Set up a variety of mocks and spies to enable simulating time and user events. Also
	 * doAnswer is used to enable AOP-like behavior to call tests before and after
	 * listeners.
	 */
	private void setupMocks() {
		Integer minParticipationId = participationEngineSettings.getTestModeMinParticipationId();

		// Replace polling the database with polling the scenarios's test queue.
		doAnswer(invocation -> pendingUnpublishParticipationQueue.poll())
				.when(constructService)
				.getNextPendingUnpublishParticipation(minParticipationId);

		// Whenever a processing date is requested, return the simulated date.
		doAnswer(invocation -> currentSimulatedDate).when(participationProcessor).getProcessingDate();

		// After participationWriter processActivation, processDeactivation, or processUnpublish
		// methods are called, check that the "processed" event was created (really a mongo call to set
		// the ParticipationItem status).
		doAnswer(invocation -> {
			invocation.callRealMethod();
			verify(constructService, times(1)).updateParticipationItemStatus(
					anyInt(),
					eq(ParticipationItemStatus.PUBLISHED),
					eq(ParticipationItemUpdateStatus.NEEDS_CLEANUP),
					any(Date.class));

			// Prep for next time it's called.
			Mockito.clearInvocations(constructService);

			return null;
		}).when(participationWriter).processActivation(any(ParticipationItemPartial.class), any(Date.class));

		doAnswer(invocation -> {
			invocation.callRealMethod();
			verify(constructService, times(1)).updateParticipationItemStatus(
					anyInt(),
					eq(ParticipationItemStatus.ARCHIVED),
					isNull(),
					any(Date.class));

			// Prep for next time it's called.
			Mockito.clearInvocations(constructService);

			return null;
		}).when(participationWriter).processDeactivation(any(ParticipationItemPartial.class), any(Date.class));

		doAnswer(invocation -> {
			invocation.callRealMethod();
			verify(constructService, times(1)).updateParticipationItemStatus(
					anyInt(),
					eq(ParticipationItemStatus.DRAFT),
					isNull(),
					any(Date.class));

			// Prep for next time it's called.
			Mockito.clearInvocations(constructService);

			return null;
		}).when(participationWriter).processUnpublish(any(ParticipationItemPartial.class), any(Date.class));

		// Set up before and after calls for when an ACTIVATE event is processed.
		doAnswer(invocation -> {
			beforeActivate(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterActivate(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationService).activateParticipation(any(ParticipationItemPartial.class), any(Date.class));

		// Set up before and after calls for when an DEACTIVATE event is processed.
		doAnswer(invocation -> {
			beforeDeactivate(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterDeactivate(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationService).deactivateParticipation(any(ParticipationItemPartial.class), any(Date.class));

		// Set up before and after calls for when an UNPUBLISH event is processed.
		doAnswer(invocation -> {
			beforeUnpublish(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterUnpublish(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationService).unpublishParticipation(any(ParticipationItemPartial.class), any(Date.class));
	}

	/**
	 * Process all events in each day, from the current date up to but not including the given future date.
	 * Use the start of the day of the current date plus 1 day so that all events scheduled
	 * for any time in that day are processed.
	 */
	private void processEventsUpTo(Date futureDate) {
		currentSimulatedDate = Date.from(
				LocalDate
						.from(currentSimulatedDate.toInstant().atZone(ZoneId.systemDefault()))
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
		participationProcessor.process();

		// Verify that all pending unpublish events were processed.
		Assertions.assertThat(pendingUnpublishParticipationQueue.size()).isEqualTo(0);
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
							.from(currentSimulatedDate.toInstant().atZone(ZoneId.systemDefault()))
							.plusDays(fixture.getStartDateOffsetDays())
							.atStartOfDay(ZoneId.systemDefault())
							.toInstant()));
		}

		if (fixture.getEndDateOffsetDays() != null) {
			// Set end date to the end of the day.
			fixture.setEndDate(Date.from(
					LocalDate
							.from(currentSimulatedDate.toInstant().atZone(ZoneId.systemDefault()))
							.plusDays(fixture.getEndDateOffsetDays() + 1)
							.atStartOfDay(ZoneId.systemDefault())
							.minus(1, ChronoUnit.MINUTES)
							.toInstant()));
		}

		beforePublish(fixture, currentSimulatedDate);
		participationTestUtilities.insertParticipationFixture(fixture);
		afterPublish(fixture, currentSimulatedDate);
	}

	private void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforePublish(fixture, processingDate));
	}

	private void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.afterPublish(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.PUBLISHED);
	}

	private void beforeActivate(ParticipationItemPartial fixture, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeActivate(fixtures.get(fixture.getParticipationId()), processingDate));
	}

	private void afterActivate(ParticipationItemPartial item, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(item.getParticipationId());
		lifecycleTests.forEach(test -> test.afterActivate(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.ACTIVATED);
	}

	private void beforeDeactivate(ParticipationItemPartial item, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeDeactivate(fixtures.get(item.getParticipationId()), processingDate));
	}

	private void afterDeactivate(ParticipationItemPartial item, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(item.getParticipationId());
		lifecycleTests.forEach(test -> test.afterDeactivate(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.DEACTIVATED);
	}

	private void beforeUnpublish(ParticipationItemPartial item, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeUnpublish(fixtures.get(item.getParticipationId()), processingDate));
	}

	private void afterUnpublish(ParticipationItemPartial item, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(item.getParticipationId());
		lifecycleTests.forEach(test -> test.afterUnpublish(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.UNPUBLISHED);
	}
}
