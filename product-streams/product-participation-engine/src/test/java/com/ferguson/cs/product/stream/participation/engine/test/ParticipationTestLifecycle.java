package com.ferguson.cs.product.stream.participation.engine.test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

/**
 * All scenario lifecycle test classes must implement this class. Each before* and
 * after* methods is default in case implementer doesn't need it.
 */
public interface ParticipationTestLifecycle {
	/**
	 * Helper to get a non-null list of expected unique ids from the fixture.
	 */
	static List<Integer> getExpectedUniqueIds(ParticipationItemFixture fixture) {
		return !CollectionUtils.isEmpty(fixture.getExpectedOwnedUniqueIds())
				? fixture.getExpectedOwnedUniqueIds()
				: (fixture.getUniqueIds() == null ? Collections.emptyList() : fixture.getUniqueIds());
	}

	default void beforePublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterPublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void beforeActivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterActivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void beforeDeactivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterDeactivate(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void beforeUnpublish(ParticipationItemFixture fixture, Date processingDate) {
	}

	default void afterUnpublish(ParticipationItemFixture fixture, Date processingDate) {
	}
}
