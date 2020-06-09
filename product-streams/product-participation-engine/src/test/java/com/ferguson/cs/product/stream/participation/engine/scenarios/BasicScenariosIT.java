package com.ferguson.cs.product.stream.participation.engine.scenarios;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.stream.participation.engine.test.ParticipationScenarioITBase;
import com.ferguson.cs.product.stream.participation.engine.test.effects.BasicWorkflowTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.effects.SchedulingTestEffectLifecycle;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationItemFixture;

public class BasicScenariosIT extends ParticipationScenarioITBase {
	@Autowired
	protected BasicWorkflowTestEffectLifecycle basicWorkflowTestEffectLifecycle;

	@Autowired
	protected SchedulingTestEffectLifecycle schedulingTestEffectLifecycle;

	/**
	 * Test scenario:
	 *   - user publishes P() - an empty participation record
	 *      of _type: participation (eg calculated discount P)
	 *   - after activation
	 *      - verify engine activation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *   - after deactivation
	 *      - verify engine deactivation event is created
	 *          - mongo status is updated
	 *          - mongo event record is added
	 *      - verify the data for the participation is removed from sql
	 *
	 * This also tests that the engine can work with a "base" participation with no effects.
	 */
	@Test
	public void engine_publish_unpublish() {
		// Make fixture participation with no schedule and no effects.
		ParticipationItemFixture p1 = ParticipationItemFixture.builder()
				.participationId(50000)
				.saleId(999)
				.build();

		// Set up scenario
		testLifecycles(basicWorkflowTestEffectLifecycle);

		// Execute scenario steps in sequence.
		manualPublish(p1);
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

		testLifecycles(schedulingTestEffectLifecycle);

		manualPublish(p1);
		advanceToDay(4);
		verifySimpleLifecycleLog(p1);
	}
}
