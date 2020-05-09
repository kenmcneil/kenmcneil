package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class SaleIdScenariosIT extends ParticipationScenarioITBase {

	/**
	 * Scenario
	 *   - user publishes P(saleId(2020), products(100, 101))
	 *
	 * Verify
	 *   - sale id has been applied to the products after activation
	 *   - sale id has been removed from the products after deactivation
	 */
	@Test
	public void engine_basicSaleIdEffect() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2020)
				.uniqueIds(100, 101)
				.scheduleByDays(0, 1)
				.build();

		useTestStrategies(basicTestLifecycle, saleIdEffectTestLifecycle);

		createUserPublishEvent(p1);
		advanceToDay(2);
		verifySimpleLifecycleLog(p1);
	}

	/**
	 * Scenario
	 *   - user publishes P1 (saleId(2000), products(100, 101), schedule(0, 10))
	 *   - user publishes P2 (saleId(2001), products(101, 102), schedule(3, 6))
	 *
	 * Verify
	 *   - when P2 activates it takes ownership of product 101 from P1 and sets sale id 2001 on it.
	 *   - when P2 deactivates, P1 takes back ownership of product 101 and sets sale id 2000 on it.
	 */
	@Test
	public void engine_overlappingSaleIdEffect() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2000)
				.uniqueIds(100, 101)
				.scheduleByDays(0, 10)
				.build();

		ParticipationItemFixture p2 = ParticipationItemFixture.builder()
				.saleId(2001)
				.uniqueIds(101, 102)
				.scheduleByDays(3, 6)
				.build();

		useTestStrategies(saleIdEffectTestLifecycle);

		createUserPublishEvent(p1);
		createUserPublishEvent(p2);
		advanceToDay(4);
		verifyParticipationOwnsExactly(p1, 100);
		advanceToDay(7);
		verifyParticipationOwnsExactly(p1, 100, 101);
		advanceToDay(11);
		verifySimpleLifecycleLog(p1, p2);
	}
}
