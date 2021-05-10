package com.ferguson.cs.product.stream.participation.engine.test.effects;

import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

import lombok.RequiredArgsConstructor;

/**
 * Verify that the basic publish transition works.
 * Check for existence of references to the participation, and verify isActive and isCoupon states.
 */
@RequiredArgsConstructor
public class CouponTestEffectLifecycle implements ParticipationTestEffectLifecycle {

	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify that the isCoupon and shouldBlockDynamicPricing flags are set
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		if (!ParticipationContentType.PARTICIPATION_COUPON_V1.equals(fixture.getContentType())) {
			return;
		}

		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial.getIsCoupon()).isNotNull();
		Assertions.assertThat(itemPartial.getShouldBlockDynamicPricing()).isNotNull();
	}
}
