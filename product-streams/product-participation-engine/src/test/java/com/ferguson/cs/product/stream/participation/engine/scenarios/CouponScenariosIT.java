package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.effects.BasicWorkflowTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.CouponTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.SaleIdTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class CouponScenariosIT extends ParticipationScenarioITBase {
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Autowired
	protected BasicWorkflowTestEffectLifecycle basicWorkFlowTestEffectLifecycle;

	@Autowired
	protected SaleIdTestEffectLifecycle saleIdTestEffectLifecycle;

	@Autowired
	protected CouponTestEffectLifecycle couponTestEffectLifecycle;

	/**
	 * Scenario
	 *   - user publishes P(saleId(2020), products(100, 101))
	 *   - user marks P as "coupon"
	 *   - user opts NOT to block dynamic pricing
	 *
	 * Verify
	 *   - sale id has been applied to the products after activation
	 *   - isCoupon and shouldBlockDynamicPricing are appropriately set
	 *   - sale id has been removed from the products after deactivation
	 */
	@Test
	public void engine_basicCouponEffect() {
		int[] uniqueIds = participationTestUtilities.getSafeTestUniqueIds();
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.saleId(2020)
				.isCoupon(true)
				.shouldBlockDynamicPricing(false)
				.scheduleByDays(0,2)
				.contentType(ParticipationContentType.PARTICIPATION_COUPON_V1)
				.build();

		testLifecycles(basicWorkFlowTestEffectLifecycle, saleIdTestEffectLifecycle, couponTestEffectLifecycle);

		createUserPublishEvent(p1);
		advanceToDay(3);
		verifySimpleLifecycleLog(p1);
	}
}
