package com.ferguson.cs.product.stream.participation.engine.scenarios;

import java.util.Date;

import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.test.BaseParticipationEngineIT;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestScenario;
import com.ferguson.cs.product.stream.participation.engine.test.ingredients.ActivationDeactivationIngredient;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ParticipationScenariosIT extends BaseParticipationEngineIT {

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

		// Create the scenario and execute scenario steps in sequence.
	    new ParticipationTestScenario()
			    .ingredients(new ActivationDeactivationIngredient())
			    .start(new Date())
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
}
