package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.BasicTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.CalculatedDiscountsTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.lifecycle.SaleIdEffectTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class CalculatedDiscountScenariosIT extends ParticipationScenarioITBase {
	@Autowired
	protected BasicTestLifecycle basicTestLifecycle;

	@Autowired
	protected SaleIdEffectTestLifecycle saleIdEffectTestLifecycle;

	@Autowired
	CalculatedDiscountsTestLifecycle calculatedDiscountsTestLifecycle;

	/**
	 * Scenario
	 *   - user publishes P(saleId(2020), products(100, 101))
	 *
	 * Verify
	 *   - sale id has been applied to the products after activation
	 *   - sale id has been removed from the products after deactivation
	 */
	@Test
	public void engine_basicCalculatedDiscountEffect() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V1)
				.saleId(2020)
				.uniqueIds(100, 101)
				.calculatedDiscounts(
						percentCalculatedDiscount(1, 10),
						percentCalculatedDiscount(22, 10)
				)
				.scheduleByDays(0, 2)
				.build();

		testLifecycles(basicTestLifecycle, saleIdEffectTestLifecycle, calculatedDiscountsTestLifecycle);
		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}
}
