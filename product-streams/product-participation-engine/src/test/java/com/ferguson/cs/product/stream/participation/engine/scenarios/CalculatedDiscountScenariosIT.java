package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.OffsalePriceFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class CalculatedDiscountScenariosIT extends ParticipationScenarioITBase {
	@Test
	public void engine_basicCalculatedDiscountV1Effect() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V1)
				.saleId(2020)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.calculatedDiscountsV1(
						percentCalculatedDiscount(1, 10),
						percentCalculatedDiscount(22, 10)
				)
				.scheduleByDays(0, 2)
				.build();

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}

	@Test
	public void engine_basicCalculatedDiscountV2Effect() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.saleId(2020)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.calculatedDiscountsV2(
						percentCalculatedDiscount(1, 10)
				)
				.scheduleByDays(0, 2)
				.build();

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}

	/**
	 * Regression test for bug where extra pricebook_cost prices are discounted during activation in
	 * participation@2 Participations; corrected in SODEV-31916. Only pb1 and pb22 pricebook prices
	 * should be modified.
	 */
	@Test
	public void engine_calculatedDiscountV2_onlyPb1And22PricesModified() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.saleId(2020)
				.uniqueIds(uniqueIds[0], uniqueIds[1])
				.calculatedDiscountsV2(
						percentCalculatedDiscount(1, 10)
				)
				.scheduleByDays(0, 2)
				.build();

		// Add a pricebook price for a product in the Participation, to verify that only
		// pb 1 and 22 pricebooks are modified.
		setNonDiscountedPricebookCosts(OffsalePriceFixture.builder()
				.uniqueId(uniqueIds[1]).pricebookId(32).price(99.95).build());

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}
}
