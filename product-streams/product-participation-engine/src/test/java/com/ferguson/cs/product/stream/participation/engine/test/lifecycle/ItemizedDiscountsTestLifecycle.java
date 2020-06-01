package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.ParticipationTestUtilities;

import lombok.RequiredArgsConstructor;

/**
 * Verify that itemized discounts effects e.g. price changes are correct.
 */
@RequiredArgsConstructor
public class ItemizedDiscountsTestLifecycle implements ParticipationTestLifecycle {
	private final ParticipationTestUtilities participationTestUtilities;
}
