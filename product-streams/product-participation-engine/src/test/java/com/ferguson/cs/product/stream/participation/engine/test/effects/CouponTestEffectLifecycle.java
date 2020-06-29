package com.ferguson.cs.product.stream.participation.engine.test.effects;

import java.util.Date;

import org.assertj.core.api.Assertions;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

import lombok.RequiredArgsConstructor;

/**
 * Verify that the basic publish transition works.
 * Check for existence of references to the participation, and verify isActive and isCoupon states.
 * Assumed case: shouldBlockDynamicPricing = false. Test for aforementioned.
 */
@RequiredArgsConstructor
public class CouponTestEffectLifecycle implements ParticipationTestEffectLifecycle {
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	private final ParticipationTestUtilities participationTestUtilities;

	/**
	 * Verify that the isCoupon and shouldBlockDynamicPricing flags are set
	 */
	@Override
	public void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
		ParticipationItemPartial itemPartial = participationTestUtilities.getParticipationItemPartial(fixture.getParticipationId());
		Assertions.assertThat(itemPartial.getIsCoupon()).isTrue();
		Assertions.assertThat(itemPartial.getShouldBlockDynamicPricing()).isFalse();
		if (fixture.getStartDate() == null || itemPartial.getStartDate() == null) {
			Assertions.assertThat(itemPartial.getStartDate()).isEqualTo(fixture.getStartDate());
		} else {
			Assertions.assertThat(itemPartial.getStartDate().getTime()).isEqualTo(fixture.getStartDate().getTime());
		}
		if (fixture.getEndDate() == null || itemPartial.getEndDate() == null) {
			Assertions.assertThat(itemPartial.getEndDate()).isEqualTo(fixture.getEndDate());
		} else {
			Assertions.assertThat(itemPartial.getEndDate().getTime()).isEqualTo(fixture.getEndDate().getTime());
		}
		Assertions.assertThat(itemPartial.getLastModifiedUserId()).isEqualTo(fixture.getLastModifiedUserId());
	}
}
