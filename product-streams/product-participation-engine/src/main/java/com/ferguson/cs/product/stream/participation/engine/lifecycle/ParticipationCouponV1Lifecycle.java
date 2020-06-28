package com.ferguson.cs.product.stream.participation.engine.lifecycle;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

import lombok.RequiredArgsConstructor;

//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
@RequiredArgsConstructor
public class ParticipationCouponV1Lifecycle implements ParticipationLifecycle{
//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	public ParticipationContentType getContentType() {
		return ParticipationContentType.PARTICIPATION_COUPON_V1;
	}
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public int publish(ParticipationItem item, Date processingDate) {
		//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return 0;
	}
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public int activate(ParticipationItemPartial itemPartial, Date processingDate) {
		//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return 0;
	}
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public int activateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return 0;
	}
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public int deactivate(ParticipationItemPartial itemPartial, Date processingDate) {
		//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return 0;
	}
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public int deactivateEffects(ParticipationItemPartial itemPartial, Date processingDate) {
		//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return 0;
	}
	//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	@Override
	public int unpublish(ParticipationItemPartial itemPartial, Date processingDate) {
		//LWH>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		return 0;
	}
}
