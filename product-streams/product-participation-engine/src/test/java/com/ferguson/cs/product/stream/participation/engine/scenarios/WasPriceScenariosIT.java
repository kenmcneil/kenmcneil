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

		setWasPrices(wasPrice0);

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}

	/**
	 * Verify that a Was price for a product is carried from sale to sale in the activate/deactivate
	 * processes when a shorter overlapping sale starts and ends during a longer sale.
	 */
	@Test
	public void engine_wasPriceCarriedToNextDiscount_overlappingParticipations() {
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

		setWasPrices(wasPrice1);

		createUserPublishEvent(p1);
		createUserPublishEvent(p2);

		advanceToDay(4);
		verifyParticipationOwnsExactly(p1, uniqueIds[0]);

		advanceToDay(7);
		verifyParticipationOwnsExactly(p1, uniqueIds[0], uniqueIds[1]);

		advanceToDay(11);
		verifySimpleLifecycleLog(p1, p2);
	}

	/**
	 * Verify that a Was price for a product is carried from sale to sale in
	 * the activate/deactivate processes when one sale starts right after the first.
	 *
	 *
	 */
	@Test
	public void engine_wasPriceCarriedToNextDiscount_backToBackParticipations() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		WasPriceFixture wasPrice1 = WasPriceFixture.builder().uniqueId(uniqueIds[1]).wasPrice(111.22).build();
		WasPriceFixture wasPrice2 = WasPriceFixture.builder().uniqueId(uniqueIds[1]).wasPrice(0.0).build();

		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V2)
				.saleId(2000)
				.itemizedV2Discounts(
						itemizedV2Discount(uniqueIds[0], 100.00),
						itemizedV2Discount(uniqueIds[1], 200.00))
				.expectedWasPrices(wasPrice1)
				.scheduleByDays(0, 1)
				.build();

		ParticipationItemFixture p2 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.saleId(2001)
				.uniqueIds(uniqueIds[1], uniqueIds[2])
				.calculatedDiscountsV2(percentCalculatedDiscount(1, 20))
				.expectedWasPrices(wasPrice1)
				.scheduleByDays(2, 3)
				.build();

		setWasPrices(wasPrice1);

		createUserPublishEvent(p1);
		createUserPublishEvent(p2);

		advanceToDay(1);
		setWasPrices(wasPrice2);

		advanceToDay(4);
		verifySimpleLifecycleLog(p1, p2);
	}

	/**
	 * Verify that a Was price for a product is not carried from sale to sale in
	 * the activate/deactivate processes when one sale starts a while after the first.
	 * In this case, it should grab the current wasPrice when the second sale starts
	 * since the gap is longer than the cool-down period.
	 *
	 *
	 */
	@Test
	public void engine_wasPriceNotCarriedToNextDiscountWhenGapBetweenParticipations() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		WasPriceFixture wasPriceNonZero = WasPriceFixture.builder().uniqueId(uniqueIds[1]).wasPrice(111.22).build();
		WasPriceFixture wasPriceZero = WasPriceFixture.builder().uniqueId(uniqueIds[1]).wasPrice(0.0).build();

		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_ITEMIZED_V2)
				.saleId(2000)
				.itemizedV2Discounts(
						itemizedV2Discount(uniqueIds[0], 100.00),
						itemizedV2Discount(uniqueIds[1], 200.00))
				.expectedWasPrices(wasPriceZero)
				.scheduleByDays(0, 1)
				.build();

		ParticipationItemFixture p2 = ParticipationItemFixture.builder()
				.contentType(ParticipationContentType.PARTICIPATION_V2)
				.saleId(2001)
				.uniqueIds(uniqueIds[1], uniqueIds[2])
				.calculatedDiscountsV2(percentCalculatedDiscount(1, 20))
				.expectedWasPrices(wasPriceNonZero)
				.scheduleByDays(3, 4)
				.build();

		setWasPrices(wasPriceZero);

		createUserPublishEvent(p1);
		createUserPublishEvent(p2);

		advanceToDay(1);
		setWasPrices(wasPriceNonZero);

		advanceToDay(5);
		verifySimpleLifecycleLog(p1, p2);
	}
}
