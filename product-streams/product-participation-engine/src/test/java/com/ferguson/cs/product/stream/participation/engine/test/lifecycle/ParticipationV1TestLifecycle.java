package com.ferguson.cs.product.stream.participation.engine.test.lifecycle;

import java.util.Collections;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

import lombok.RequiredArgsConstructor;

/**
 */
@RequiredArgsConstructor
public class ParticipationV1TestLifecycle implements ParticipationTestLifecycle {
	/**
	 * Helper to get a non-null list of expected unique ids from the fixture.
	 */
	public static List<Integer> getExpectedUniqueIds(ParticipationItemFixture fixture) {
		return fixture.getUniqueIds() == null ? Collections.emptyList() : fixture.getUniqueIds();
	}
}
