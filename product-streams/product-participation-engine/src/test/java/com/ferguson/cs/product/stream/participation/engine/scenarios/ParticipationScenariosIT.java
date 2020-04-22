package com.ferguson.cs.product.stream.participation.engine.scenarios;

import static org.mockito.Mockito.spy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationService;
import com.ferguson.cs.product.stream.participation.engine.ParticipationServiceImpl;
import com.ferguson.cs.product.stream.participation.engine.ParticipationWriter;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.test.BaseParticipationEngineIT;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestScenario;
import com.ferguson.cs.product.stream.participation.engine.test.ingredients.ActivationDeactivationIngredient;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ParticipationScenariosIT extends BaseParticipationEngineIT {
	@Autowired
	public ParticipationDao participationDao;

	@MockBean
	public ConstructService constructService;

	public ParticipationService participationService;
	public ParticipationWriter participationWriter;
	public ParticipationProcessor participationProcessor;

	/**
	 * Set up dependencies and wire up classes manually.
	 *
	 * No mocking/spying needed:
	 *      ParticipationDao
	 *      participationService
	 *
	 * Mock entire class
	 *      ConstructService
	 *
	 * Spy on class to override specific methods or do things before/after a method is called
	 *      participationWriter
	 *      participationProcessor
	 *
	 */
	@Before
	public void before() {
		disableLocalCache();
		MockitoAnnotations.initMocks(this);

		participationService = new ParticipationServiceImpl(participationDao);
		participationWriter = spy(new ParticipationWriter(participationService, constructService));
		participationProcessor = spy(new ParticipationProcessor(constructService, participationWriter));
	}

	/**
	 * Test scenario:
	 *   - user publishes P() - an empty participation record
	 *   - after activation
	 *      - verify engine activation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *   - after deactivation
	 *      - verify engine deactivation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *      - verify the data for the participation is removed from sql
	 */
	@Test
	public void engine_basicPublishAndUnpublish() {
		// Make fixture participation with no schedule and no effects.
	    // Currently saleId is required because it's not a nullable value in the database.
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(50000)
				.build();

	    LocalDate today = LocalDate.now();
	    LocalDate tomorrow = today.plusDays(10);

		// Create the scenario and execute scenario steps in sequence.
	    scenario()
			    .ingredients(new ActivationDeactivationIngredient())
			    .start(Date.from(tomorrow.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
			    .createUserPublishEvent(p1)
			    .processEvents()
			    .createUserUnpublishEvent(p1)
	            .processEvents();
	}

	/**
	 * Test scenario:
	 *   - user publishes P(products(1, 2), saleId(3333))
	 *   - after activation
	 *      - verify sale id is applied to the products
	 *      - verify engine activation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *   - after deactivation
	 *      - verify sale id is removed from the products at deactivation
	 *      - verify engine deactivation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *      - verify the data for the participation is removed from sql
	 */
//	@Test
//	public void engine_basicSaleId_() {
//		// Make fixture participation with no effects.
//		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
//				.participationId(5000)
//				.scheduleByDays(1, 3)
//				.build();
//
//		// Create the scenario.
//		new ParticipationTestScenario()
//				.ingredients(
//						new ActivationDeactivationIngredient(),
//						new SaleIdEffectIngredient()
//				)
//				.start(0)
//				.createUserPublishEvent(p1)
//
//				.advanceToDay(4);
//	}



	/**
	 * Create a new scenario instance and initialize it.
	 */
	private ParticipationTestScenario scenario() {
		ParticipationTestScenario scenario = new ParticipationTestScenario(
				constructService,
				participationService,
				participationProcessor,
				participationWriter,
				participationTestUtilities
		);
		scenario.setupMocks();
		return scenario;
	}
}
