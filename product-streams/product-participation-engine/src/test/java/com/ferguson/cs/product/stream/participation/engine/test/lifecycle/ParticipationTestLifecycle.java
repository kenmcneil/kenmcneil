package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

/**
 * Any methods that are specific to a type of Participation should be in a class implementing this interface.
 */
public interface ParticipationTestLifecycle {
	/**
	 * Helper to get a non-null list of expected unique ids from the fixture. If the fixture has expected uniqueIds
	 * populated then return that list, else return the list from the type-specific method.
	 */
	static List<Integer> getExpectedUniqueIds(ParticipationItemFixture fixture) {
		if (!CollectionUtils.isEmpty(fixture.getExpectedOwnedUniqueIds())) {
			return fixture.getExpectedOwnedUniqueIds();
		}

		if (ParticipationContentType.PARTICIPATION_V1.equals(fixture.getContentType())
				|| ParticipationContentType.PARTICIPATION_V2.equals(fixture.getContentType())) {
			return ParticipationV1V2TestLifecycle.getUniqueIds(fixture);
		}

		if (ParticipationContentType.PARTICIPATION_ITEMIZED_V1.equals(fixture.getContentType())
				|| ParticipationContentType.PARTICIPATION_ITEMIZED_V2.equals(fixture.getContentType())) {
			return ParticipationItemizedV1V2TestLifecycle.getUniqueIdsFromItemizedDiscounts(fixture);
		}

		if (ParticipationContentType.PARTICIPATION_COUPON_V1.equals(fixture.getContentType())) {
			return ParticipationCouponV1TestLifecycle.getUniqueIds(fixture);
		}

		return Collections.emptyList();
	}
}
