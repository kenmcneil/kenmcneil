package com.ferguson.cs.product.stream.participation.engine.scenarios;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.scenarios.ingredients.ActivationDeactivationIngredient;
import com.ferguson.cs.product.stream.participation.engine.test.BaseParticipationEngineIT;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestScenario;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ParticipationScenariosIT extends BaseParticipationEngineIT {

	@Before
	public void before() {
//		disableLocalCache();
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
		// Make fixture participation with no effects.
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(5000)
				.build();

		// Create the scenario.
	    ParticipationTestScenario scenario = ParticipationTestScenario.builder()
				.ingredients(
						new ActivationDeactivationIngredient()
				)
			    .build();

	    // Execute scenario steps in sequence.
	    scenario.userPublishEvent(p1)
			    .processEvents()
			    .userUnpublishEvent(p1)
				.onDay(1)
				.onDay(2);
//	            .participationActive(5000)
//			    .endOnDay(5);
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
	@Test
	public void engine_basicSaleId_() {
		// Make fixture participation with no effects.
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(5000)
//				.schedule(new Date(), new Date())
				.build();

		// Create the scenario.
		ParticipationTestScenario scenario = ParticipationTestScenario.builder()
				.ingredients(
						new ActivationDeactivationIngredient()
				)
				.runDate(new Date())
				.build();

		// Execute scenario steps in sequence.
		scenario.onDay(0)
				.userPublishEvent(p1)
				.onDay(1)
				.onDay(2);
//	            .participationActive(5000)
//			    .endOnDay(5);
	}
}
