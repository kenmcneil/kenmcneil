package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class BasicScenariosIT extends ParticipationScenarioITBase {
	/**
	 * Test scenario:
	 *   - user publishes P() - an empty participation record
	 *   - after activation
	 *      - verify engine activation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *   - after deactivation
	 *      - verify engine deactivation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *      - verify the data for the participation is removed from sql
	 */
	@Test
	public void basic_publish_unpublish() {
		// Make fixture participation with no schedule and no effects.
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(50000)
				.build();

		// Set up scenario
		useTestStrategies(basicLifecycleTestStrategy);

		// Execute scenario steps in sequence.
	    createUserPublishEvent(p1);
	    processEvents();
	    createUserUnpublishEvent(p1);
	    processEvents();
		verifySimpleLifecycleLog(p1);
	}

	/**
	 * Test scheduled activation and deactivation.
	 */
	@Test
	public void engine_basicScheduling() {
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(50000)
				.scheduleByDays(1, 3)
				.build();

		useTestStrategies(schedulingLifecycleTestStrategy);

		createUserPublishEvent(p1);
		advanceToDay(4);
		verifySimpleLifecycleLog(p1);
	}
}
