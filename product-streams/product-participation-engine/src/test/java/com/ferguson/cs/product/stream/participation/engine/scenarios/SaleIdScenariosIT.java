package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class SaleIdScenariosIT extends ParticipationScenarioITBase {

	/**
	 * Test scenario:
	 *   - user publishes P(saleId(2020), products(100, 101))
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
	public void engine_basicSaleIdEffect() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(50000)
				.saleId(2020)
				.uniqueIds(100, 101)
				.scheduleByDays(0, 1)
				.build();

		// Create the scenario.
		useTestStrategies(basicLifecycleTestStrategy, saleIdEffectLifecycleTestStrategy);
		createUserPublishEvent(p1);
		advanceToDay(2);
		verifySimpleLifecycleLog(p1);
	}
}
