package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;

public interface ParticipationScenarioIngredient {
	void beforeActivation(ParticipationItemFixture p, Date runDate);
}
