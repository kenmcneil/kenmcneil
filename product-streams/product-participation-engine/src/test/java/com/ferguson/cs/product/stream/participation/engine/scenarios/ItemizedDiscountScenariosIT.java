package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.effects.WorkflowTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.ItemizedDiscountsTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.SaleIdTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ItemizedDiscountScenariosIT extends ParticipationScenarioITBase {
	@Autowired
	protected WorkflowTestEffectLifecycle workflowTestEffectLifecycle;

	@Autowired
	protected SaleIdTestEffectLifecycle saleIdTestEffectLifecycle;

	@Autowired
	ItemizedDiscountsTestEffectLifecycle itemizedDiscountsTestEffectLifecycle;

	/**
	 * Scenario
	 *   - user publishes P(saleId(2020), products(100, 101))
	 *
	 * Verify
	 *   - sale id has been applied to the products after activation
	 *   - sale id has been removed from the products after deactivation
	 */
	@Test
	public void engine_basicItemizedDiscountEffect() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2020)
				.itemizedDiscounts(
					itemizedDiscount(100, 200.00, 100.00)
				)
				.scheduleByDays(0, 2)
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
				.build();

		testLifecycles(workflowTestEffectLifecycle, saleIdTestEffectLifecycle, itemizedDiscountsTestEffectLifecycle);

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}
}
