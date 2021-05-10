package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class ItemizedDiscountScenariosIT extends ParticipationScenarioITBase {
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
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2020)
				.itemizedV1Discounts(
						itemizedV1Discount(uniqueIds[0], 200.00, 100.00)
				)
				.scheduleByDays(0, 2)
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V1)
				.build();

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}
}
