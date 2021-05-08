package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.effects.CalculatedDiscountsV2TestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.ItemizedDiscountsV2TestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.WasPriceFixture;

public class WasPriceScenariosIT extends ParticipationScenarioITBase {
	@Autowired
	protected CalculatedDiscountsV2TestEffectLifecycle calculatedDiscountsV2TestEffectLifecycle;

	@Autowired
	protected ItemizedDiscountsV2TestEffectLifecycle itemizedDiscountsV2TestEffectLifecycle;

	/**
	 * Verify that a valid Was price for a product is set in the pricebook_cost record.
	 */
	@Test
	public void engine_wasPrice_participationV2() {
		// A calculated discount for a product that has a valid Was price.
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		WasPriceFixture wasPrice0 = WasPriceFixture.builder().uniqueId(uniqueIds[0]).wasPrice(111.22).build();

		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.saleId(2020)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.calculatedDiscountsV2(percentCalculatedDiscount(1, 10))
				.expectedWasPrices(wasPrice0)
				.scheduleByDays(0, 2)
				.build();

		testLifecycles(calculatedDiscountsV2TestEffectLifecycle);
		withWasPrices(wasPrice0);

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}

	/**
	 * Verify that a Was price for a product is carried from sale to sale in
	 * the activate/deactivate processes.
	 *
	 * Scenario
	 *   - user publishes P1 (saleId(2000), products(0, 1), schedule(0, 10))
	 *   - user publishes P2 (saleId(2001), products(1, 2), schedule(3, 6))
	 *
	 * Verify
	 *   - when P2 activates it takes ownership of product 101 from P1 and sets sale id 2001 on it.
	 *   - when P2 deactivates, P1 takes back ownership of product 101 and sets sale id 2000 on it.
	 */
	@Test
	public void engine_wasPrice_carriedToNextDiscount() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		WasPriceFixture wasPrice1 = WasPriceFixture.builder()
				.uniqueId(uniqueIds[1]).wasPrice(111.22).build();

		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V2)
				.saleId(2000)
				.itemizedV2Discounts(
						itemizedV2Discount(uniqueIds[0], 100.00),
						itemizedV2Discount(uniqueIds[1], 200.00))
				.expectedWasPrices(wasPrice1)
				.scheduleByDays(0, 10)
				.build();

		ParticipationItemFixture p2 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.saleId(2001)
				.uniqueIds(uniqueIds[1], uniqueIds[2])
				.calculatedDiscountsV2(percentCalculatedDiscount(1, 20))
				.expectedWasPrices(wasPrice1)
				.scheduleByDays(3, 6)
				.build();

		testLifecycles(calculatedDiscountsV2TestEffectLifecycle, itemizedDiscountsV2TestEffectLifecycle);
		withWasPrices(wasPrice1);

		createUserPublishEvent(p1);
		createUserPublishEvent(p2);

		advanceToDay(4);
		verifyParticipationOwnsExactly(p1, uniqueIds[0]);

		advanceToDay(7);
		verifyParticipationOwnsExactly(p1, uniqueIds[0], uniqueIds[1]);

		advanceToDay(11);
		verifySimpleLifecycleLog(p1, p2);
	}
}
