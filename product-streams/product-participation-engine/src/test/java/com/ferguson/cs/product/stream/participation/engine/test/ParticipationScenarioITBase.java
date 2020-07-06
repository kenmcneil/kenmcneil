package com.ferguson.cs.product.stream.participation.engine.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ferguson.cs.product.stream.participation.engine.ParticipationEngineSettings;
import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationWriter;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationCoreDao;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationV1Dao;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemSchedule;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.product.stream.participation.engine.test.effects.BasicWorkflowTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.CalculatedDiscountsTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.CouponTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.ItemizedDiscountsTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.ParticipationTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.SaleIdTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.SchedulingTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.CalculatedDiscountFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.CouponFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ItemizedDiscountFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.LifecycleState;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

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
		public BasicWorkflowTestEffectLifecycle activationDeactivationLifecycleTest (
				ParticipationTestUtilities participationTestUtilities
		) {
			return new BasicWorkflowTestEffectLifecycle(participationTestUtilities);
		}

		@Bean
		public SchedulingTestEffectLifecycle schedulingLifecycleTest (
				ParticipationTestUtilities participationTestUtilities
		) {
			return new SchedulingTestEffectLifecycle(participationTestUtilities);
		}

		@Bean
		public SaleIdTestEffectLifecycle saleIdEffectLifecycleTest (
				ParticipationTestUtilities participationTestUtilities
		) {
			return new SaleIdTestEffectLifecycle(participationTestUtilities);
		}

		@Bean
		public CalculatedDiscountsTestEffectLifecycle calculatedDiscountsTestLifecycle (
				ParticipationTestUtilities participationTestUtilities
		) {
			return new CalculatedDiscountsTestEffectLifecycle(participationTestUtilities);
		}

		@Bean
		public ItemizedDiscountsTestEffectLifecycle itemizedDiscountsTestLifecycle (
				ParticipationTestUtilities participationTestUtilities
		) {
			return new ItemizedDiscountsTestEffectLifecycle(participationTestUtilities);
		}

		@Bean
		public CouponTestEffectLifecycle couponTestEffectLifeCycle (
				ParticipationTestUtilities participationTestUtilities
		) {
			return new CouponTestEffectLifecycle(participationTestUtilities);
		}
	}

	/*
	 * No mocking/spying needed:
	 *      ParticipationCoreDao
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
	protected ParticipationCoreDao participationCoreDao;

	@Autowired
	protected ParticipationV1Dao participationV1Dao;

	@Autowired
	protected ParticipationEngineSettings participationEngineSettings;

	@MockBean
	protected ConstructService constructService;

	@SpyBean
	protected ParticipationLifecycleService participationLifecycleService;

	@SpyBean
	protected ParticipationWriter participationWriter;

	@SpyBean
	protected ParticipationProcessor participationProcessor;

	// For converting a ParticipationItemFixture to a ParticipationItem
	private final ObjectMapper mapper = new ObjectMapper();

	// Properties to track Scenario test state.
	protected Date originalSimulatedDate;
	protected Date currentSimulatedDate;
	protected List<ParticipationTestEffectLifecycle> lifecycleTests;
	protected Map<Integer, ParticipationItemFixture> fixtures = new HashMap<>();
	protected Queue<ParticipationItem> pendingUnpublishParticipationQueue = new LinkedList<>();
	protected Queue<ParticipationItem> pendingPublishParticipationQueue = new LinkedList<>();

	private boolean ranBeforeAll = false;

	@Before
	public void before() {
		super.before();

		// Perform before-all initialization.
		if (!ranBeforeAll) {
			setupMocks();
			ranBeforeAll = true;
		}

		// Default the simulated scenario start date.
		originalSimulatedDate = new Date();
		currentSimulatedDate = originalSimulatedDate;
	}

	public void testLifecycles(ParticipationTestEffectLifecycle... params) {
		lifecycleTests = Arrays.asList(params);
	}

	public void startOn(Date simulatedRunDate) {
		originalSimulatedDate = simulatedRunDate;
		currentSimulatedDate = simulatedRunDate;
	}

	public void advanceToDay(int dayNumber) {
		Date futureDate = new Date(originalSimulatedDate.getTime() + TimeUnit.DAYS.toMillis(dayNumber));
		Assertions.assertThat(futureDate).isAfterOrEqualsTo(currentSimulatedDate);
		processEventsUpTo(futureDate);
	}

	/**
	 * Keep track of fixtures used so they may be accessed by lifecycle tests for verification.
	 * Ensure critical values are set or defaulted, e.g. participationId and lastModifiedUserId.
	 * Assert that this participation has not been added yet.
	 */
	public void initAndRememberFixture(ParticipationItemFixture fixture) {
		participationTestUtilities.validateAndSetDefaults(fixture);
		Assertions.assertThat(fixtures.containsKey(fixture.getParticipationId())).isFalse();
		fixtures.put(fixture.getParticipationId(), fixture);
	}

	/**
	 * Publish the fixture "manually"; insert fixture data directly to SQL. Inserts
	 * the base Participation record into the participationItemPartial table, and
	 * insert any other data added to the fixture such as uniqueIds. Simulating the
	 * publish event makes it possible to create scenario tests without making
	 * a complete specific type of Participation, to allow testing base, non-effect,
	 * engine behavior.
	 */
	public void manualPublish(ParticipationItemFixture fixture) {
		initAndRememberFixture(fixture);
		simulatePublishEvent(fixture);
	}

	/**
	 * Simulate a user publish event; create a ParticipationItem with content and all
	 * needed properties and add it to the simulated publish queue where it will be
	 * consumed by getNextPendingPublishParticipation.
	 */
	public void createUserPublishEvent(ParticipationItemFixture fixture) {
		initAndRememberFixture(fixture);
		pendingPublishParticipationQueue.add(fixtureToParticipationItem(fixture));
	}

	/**
	 * Simulate a user unpublish event. Adds to list of records that will be returned by
	 * constructService.getNextPendingUnpublishParticipation.
	 */
	public void createUserUnpublishEvent(ParticipationItemFixture fixture) {
		ParticipationItem p = new ParticipationItem();
		p.setId(fixture.getParticipationId());
		p.setLastModifiedUserId(fixture.getLastModifiedUserId());
		pendingUnpublishParticipationQueue.add(p);
	}

	/**
	 * Process any pending events present at the current simulated date. This does not advance time.
	 * Use when either time advancing is not important, or events need to be processed again before
	 * advancing time.
	 */
	public void processEvents() {
		processEventsAtCurrentSimulatedDate();
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
		List<Integer> ownedUniqueIds = participationTestUtilities.getOwnedUniqueIds(fixture.getParticipationId());
		Assertions.assertThat(ownedUniqueIds).containsExactlyInAnyOrderElementsOf(Arrays.asList(expectedUniqueIds));
	}

	public void logSaleIds(ParticipationItemFixture fixture) {
		List<Integer> ownedUniqueIds = participationTestUtilities.getOwnedUniqueIds(fixture.getParticipationId());
		String ownedSaleIds = participationTestUtilities.getProductSaleParticipations(ownedUniqueIds).stream()
				.map(psp -> "(" + psp.getUniqueId() + ": " + psp.getSaleId().toString() + ")")
				.collect(Collectors.joining(", "));
		System.out.println(fixture.getParticipationId() + " sale ids(" + ownedSaleIds + ")");
	}

	/**
	 * Set up a variety of mocks and spies to enable simulating time and user events. Also
	 * doAnswer is used to enable AOP-like behavior to call tests before and after
	 * listeners.
	 */
	private void setupMocks() {
		// Replace polling the mongodb database with polling the scenarios's test queues.
		// Restrict to only process Participation ids in the IT test range.
		doAnswer(invocation -> pendingPublishParticipationQueue.poll())
				.when(constructService)
				.getNextPendingPublishParticipation(participationTestUtilities.getFirstTestParticipationId());
		doAnswer(invocation -> pendingUnpublishParticipationQueue.poll())
				.when(constructService)
				.getNextPendingUnpublishParticipation(participationTestUtilities.getFirstTestParticipationId());

		// Whenever a processing date is requested, return the simulated date.
		doAnswer(invocation -> currentSimulatedDate).when(participationProcessor).getProcessingDate();

		// After participationWriter processPublish, processActivation, processDeactivation, or processUnpublish
		// methods are called, check that the "processed" event was created (really a mongo call to set
		// the ParticipationItem statuses).
		doAnswer(invocation -> {
			invocation.callRealMethod();
			verify(constructService, times(1)).updateParticipationItemStatus(
					anyInt(),
					eq(ParticipationItemStatus.PUBLISHED),
					eq(ParticipationItemUpdateStatus.NEEDS_UPDATE),
					any(Date.class),
					anyInt());

			// Prep for next time it's called.
			Mockito.clearInvocations(constructService);

			return null;
		}).when(participationWriter).processPublish(any(ParticipationItem.class), any(Date.class));

		doAnswer(invocation -> {
			invocation.callRealMethod();
			verify(constructService, times(1)).updateParticipationItemStatus(
					anyInt(),
					eq(ParticipationItemStatus.PUBLISHED),
					eq(ParticipationItemUpdateStatus.NEEDS_CLEANUP),
					any(Date.class),
					anyInt());

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
					any(Date.class),
					anyInt());

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
					any(Date.class),
					anyInt());

			// Prep for next time it's called.
			Mockito.clearInvocations(constructService);

			return null;
		}).when(participationWriter).processUnpublish(any(ParticipationItemPartial.class), any(Date.class));

		// Set up before and after calls for when a PUBLISH event is processed.
		doAnswer(invocation -> {
			beforePublish(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterPublish(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationLifecycleService).publishByType(any(ParticipationItem.class), any(Date.class));

		// Set up before and after calls for when an ACTIVATE event is processed.
		doAnswer(invocation -> {
			beforeActivate(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterActivate(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationLifecycleService).activateByType(any(ParticipationItemPartial.class), any(Date.class));

		// Set up before and after calls for when an DEACTIVATE event is processed.
		doAnswer(invocation -> {
			beforeDeactivate(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterDeactivate(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationLifecycleService).deactivateByType(any(ParticipationItemPartial.class), any(Date.class));

		// Set up before and after calls for when an UNPUBLISH event is processed.
		doAnswer(invocation -> {
			beforeUnpublish(invocation.getArgument(0), invocation.getArgument(1));
			invocation.callRealMethod();
			afterUnpublish(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		}).when(participationLifecycleService).unpublishByType(any(ParticipationItemPartial.class), any(Date.class));
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

		// Verify that all pending publish and unpublish events were processed.
		Assertions.assertThat(pendingPublishParticipationQueue.size()).isEqualTo(0);
		Assertions.assertThat(pendingUnpublishParticipationQueue.size()).isEqualTo(0);
	}

	private Date dateOffsetByDaysAtStartOfDay(Date from, int days) {
		return Date.from(LocalDate.from(from.toInstant().atZone(ZoneId.systemDefault()))
				.plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private Date dateOffsetByDaysAtEndOfDay(Date from, int days) {
		return Date.from(LocalDate.from(from.toInstant().atZone(ZoneId.systemDefault()))
				.plusDays(days + 1).atStartOfDay(ZoneId.systemDefault())
				.minus(1, ChronoUnit.MINUTES).toInstant());
	}

	/**
	 * Converts the fixture into a ParticipationItem as if it were just published in Construct.
	 * Converts day offsets to actual dates if offsets were used.
	 */
	private void simulatePublishEvent(ParticipationItemFixture fixture) {
		// If day offsets were used, convert to actual dates offset from the current run date.
		if (fixture.getStartDateOffsetDays() != null) {
			fixture.setStartDate(dateOffsetByDaysAtStartOfDay(currentSimulatedDate, fixture.getStartDateOffsetDays()));
		}
		if (fixture.getEndDateOffsetDays() != null) {
			fixture.setEndDate(dateOffsetByDaysAtEndOfDay(currentSimulatedDate, fixture.getEndDateOffsetDays()));
		}

		ParticipationItem item = ParticipationItem.builder()
				.id(fixture.getParticipationId())
				.saleId(fixture.getSaleId())
				.description(fixture.toString())
				.schedule(new ParticipationItemSchedule(fixture.getStartDate(), fixture.getEndDate()))
				.status(ParticipationItemStatus.PUBLISHED)
				.lastModifiedUserId(fixture.getLastModifiedUserId())
				.lastModifiedDate(currentSimulatedDate)
				.updateStatus(ParticipationItemUpdateStatus.NEEDS_UPDATE)
				.build();

		beforePublish(item, currentSimulatedDate);
		participationTestUtilities.insertParticipationFixture(fixture);
		afterPublish(item, currentSimulatedDate);
	}

	/**
	 * Converts the fixture into a ParticipationItem as if it were just published in Construct.
	 * Converts day offsets to actual dates if offsets were used.
	 */
	private ParticipationItem fixtureToParticipationItem(ParticipationItemFixture fixture) {
		// If day offsets were used, convert to actual dates offset from the current run date.
		if (fixture.getStartDateOffsetDays() != null) {
			fixture.setStartDate(dateOffsetByDaysAtStartOfDay(currentSimulatedDate, fixture.getStartDateOffsetDays()));
		}
		if (fixture.getEndDateOffsetDays() != null) {
			fixture.setEndDate(dateOffsetByDaysAtEndOfDay(currentSimulatedDate, fixture.getEndDateOffsetDays()));
		}

		ParticipationItem item = ParticipationItem.builder()
				.id(fixture.getParticipationId())
				.saleId(fixture.getSaleId())
				.description(fixture.toString())
				.schedule(new ParticipationItemSchedule(fixture.getStartDate(), fixture.getEndDate()))
				.status(ParticipationItemStatus.PUBLISHED)
				.lastModifiedUserId(fixture.getLastModifiedUserId())
				.lastModifiedDate(currentSimulatedDate)
				.updateStatus(ParticipationItemUpdateStatus.NEEDS_PUBLISH)
				.build();
		if ("participation@1".equals(fixture.getContentType().nameWithMajorVersion())) {
			// check requirements of this type of participation
			//TODO make calculatedDiscount content required here after Coupon type goes live
			Assertions.assertThat(fixture.getSaleId()).isNotZero();
			Assertions.assertThat(fixture.getUniqueIds()).isNotEmpty();
			// set the content object
			item.setContent(getParticipationV1Content(fixture));
		} else if ("participation-itemized@1".equals(fixture.getContentType().nameWithMajorVersion())) {
			Assertions.assertThat(fixture.getSaleId()).isNotZero();
			Assertions.assertThat(fixture.getUniqueIds()).isNullOrEmpty();
			Assertions.assertThat(fixture.getItemizedDiscountFixtures()).isNotEmpty();
			// set the content object
			item.setContent(getParticipationItemizedV1Content(fixture));
		} else if ("participation-coupon@1".equals(fixture.getContentType().nameWithMajorVersion())) {
			//TEMP values are good here
			Assertions.assertThat(fixture.getSaleId()).isNotZero();
			Assertions.assertThat(fixture.getUniqueIds()).isNotEmpty();
			// set the content object
			item.setContent(getParticipationCouponV1Content(fixture));
		} else {
			Assertions.fail("Unknown content type in %s", fixture.toString());
		}

		return item;
	}

	private ObjectNode atPath(ObjectNode obj, String format, String... parts) {
		return (ObjectNode)obj.at(String.format(format, (Object[])parts));
	}

	/**
	 * Build the content map for the given fixture of type participation@1, as if it came from Construct.
	 */
	private Map<String, Object> getParticipationV1Content(ParticipationItemFixture fixture) {
		ObjectNode content;

		// Calculated discount values are optional. Load the matching template and fill in any
		// discounts.
		List<CalculatedDiscountFixture> discounts = fixture.getCalculatedDiscountFixtures();
		if (!CollectionUtils.isEmpty(discounts)) {
			CalculatedDiscountFixture discount1 = discounts.get(0);
			CalculatedDiscountFixture discount22 = discounts.get(0);
			content = discount1.getIsPercent()
					? getContentTemplate("participationV1-content-percent-discount.json")
					: getContentTemplate("participationV1-content-amount-discount.json");
			String discountTypeFieldName = discount1.getIsPercent() ? "percentDiscount" : "amountDiscount";
			String pathToDiscount = "/priceDiscounts/calculatedDiscount/" + discountTypeFieldName + "/%s";
			atPath(content, pathToDiscount, "template").put("selected", discount1.getTemplateId());
			atPath(content, pathToDiscount, "pricebookId1").put("text", discount1.getDiscountAmount().toString());
			atPath(content, pathToDiscount, "pricebookId22").put("text", discount22.getDiscountAmount().toString());
		} else {
			content = getContentTemplate("participationV1-content-no-discount.json");
		}

		// Set the required values in content. It's ok to use only the major version, as in "participation@1"
		// instead of "participation@1.0.0", since the minor and patch versions indicate non-breaking changes
		// in the major version. Records from Construct would have the specific @x.y.z version that was used
		// to edit the record, but since the minor and patch is ignored in the engine it works either way.
		content.put("_type", fixture.getContentType().nameWithMajorVersion());
		atPath(content, "/productSale").put("saleId", fixture.getSaleId());
		atPath(content, "/calculatedDiscounts/uniqueIds").set("list", mapper.valueToTree(fixture.getUniqueIds()));

		return mapper.convertValue(content, new TypeReference<Map<String, Object>>(){});
	}

	/**
	 * Build the content map for the given fixture of type @itemized-participation@1, as if it came from Construct.
	 * Requires the fixture to include at least two list entries (pb1 and pb22, in that order). Additional entries
	 * will be ignored.
	 */
	private Map<String, Object> getParticipationItemizedV1Content(ParticipationItemFixture fixture) {
		ObjectNode content;

		// Itemized discount values are not optional. Load the matching template and fill in any
		// discounts.
		List<ItemizedDiscountFixture> discounts = fixture.getItemizedDiscountFixtures();
		List<List<Object>> mappedDiscounts = discounts.stream()
									.map(discount-> {
										List<Object> row = new ArrayList<>();
										row.add(discount.getUniqueId());
										row.add("Manufacturer for uniqueId " + discount.getUniqueId());
										row.add(discount.getPricebook1Price());
										row.add(discount.getPricebook22Price());
										return row;
									})
									.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(discounts)) {
			Assertions.fail("Missing required itemized discount content in %s", fixture.toString());
		}
		content = getContentTemplate("participationItemizedV1-content-discount.json");
		atPath(content, "/itemizedDiscounts").set("list", mapper.valueToTree(mappedDiscounts));
		// Set the required values in content. It's ok to use only the major version, as in "participation@1"
		// instead of "participation@1.0.0", since the minor and patch versions indicate non-breaking changes
		// in the major version. Records from Construct would have the specific @x.y.z version that was used
		// to edit the record, but since the minor and patch is ignored in the engine it works either way.
		content.put("_type", fixture.getContentType().nameWithMajorVersion());
		atPath(content, "/productSale").put("saleId", fixture.getSaleId());

		return mapper.convertValue(content, new TypeReference<Map<String, Object>>(){});
	}

	/**
	 * Build the content map for the given fixture of type participation-coupon@1, as if it came from Construct.
	 */
	private Map<String, Object> getParticipationCouponV1Content(ParticipationItemFixture fixture) {
		ObjectNode content;

		CouponFixture coupon = fixture.getCouponFixture();

		content = getContentTemplate("participationCouponV1-content.json");

		// Set the required values in content. It's ok to use only the major version, as in "participation-coupon@1"
		// instead of "participation-coupon@1.0.0", since the minor patch versions indicate non-breaking changes
		// in the major version. Records from Construct would have the specific @x.y.z version that was used
		// to edit the record, but since the minor and patch is ignored in the engine, it just works.
		content.put("_type", fixture.getContentType().nameWithMajorVersion());
		atPath(content, "/productSale").put( "saleId", fixture.getSaleId());
		atPath(content, "/isCoupon").put("value", fixture.getIsCoupon());
		atPath(content, "/shouldBlockDynamicPricing").put("value", fixture.getShouldBlockDynamicPricing());
		atPath(content, "/uniqueIds").set("list", mapper.valueToTree(fixture.getUniqueIds()));

		return mapper.convertValue(content, new TypeReference<Map<String, Object>>(){});
	}

	private ObjectNode getContentTemplate(String templateFilename) {
		String path = "src/test/resources/" + templateFilename;
		try {
			FileInputStream fis = new FileInputStream(path);
			String contentTemplate = IOUtils.toString(fis, StandardCharsets.UTF_8);
			return (ObjectNode)mapper.readTree(contentTemplate);
		} catch (Exception e) {
			throw new AssertionError("Error loading and deserializing content template file " + path, e);
		}
	}

	private void beforePublish(ParticipationItem item, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforePublish(fixtures.get(item.getId()), processingDate));
	}

	private void afterPublish(ParticipationItem item, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(item.getId());
		lifecycleTests.forEach(test -> test.afterPublish(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.PUBLISHED);
	}

	private void beforeActivate(ParticipationItemPartial itemPartial, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeActivate(fixtures.get(itemPartial.getParticipationId()), processingDate));
	}

	private void afterActivate(ParticipationItemPartial itemPartial, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(itemPartial.getParticipationId());
		lifecycleTests.forEach(test -> test.afterActivate(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.ACTIVATED);
	}

	private void beforeDeactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeDeactivate(fixtures.get(itemPartial.getParticipationId()), processingDate));
	}

	private void afterDeactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(itemPartial.getParticipationId());
		lifecycleTests.forEach(test -> test.afterDeactivate(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.DEACTIVATED);
	}

	private void beforeUnpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		lifecycleTests.forEach(test -> test.beforeUnpublish(fixtures.get(itemPartial.getParticipationId()), processingDate));
	}

	private void afterUnpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		ParticipationItemFixture fixture = fixtures.get(itemPartial.getParticipationId());
		lifecycleTests.forEach(test -> test.afterUnpublish(fixture, processingDate));
		fixture.getStateLog().add(LifecycleState.UNPUBLISHED);
	}
}
