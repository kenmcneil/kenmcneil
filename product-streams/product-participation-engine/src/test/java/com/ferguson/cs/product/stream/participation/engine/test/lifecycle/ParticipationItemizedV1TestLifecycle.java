package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ferguson.cs.product.stream.participation.engine.test.model.ItemizedDiscountFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

import lombok.RequiredArgsConstructor;

/**
 *
 */
@RequiredArgsConstructor
public class ParticipationItemizedV1TestLifecycle implements ParticipationTestLifecycle {
	/**
	 * Helper to get a non-null list of expected unique ids from the fixture.
	 */
	public static List<Integer> getUniqueIdsFromItemizedDiscounts(ParticipationItemFixture fixture) {
		return fixture.getItemizedDiscountFixtures() == null ? Collections.emptyList()
						: fixture.getItemizedDiscountFixtures()
								.stream()
								.map(ItemizedDiscountFixture::getUniqueId)
								.collect(Collectors.toList());
	}
}
