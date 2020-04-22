package com.ferguson.cs.product.stream.participation.engine.test.model;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

public interface ParticipationScenarioIngredient {

	void beforeUnpublish(ParticipationItem item, Date processingDate);

	void afterUnpublish(ParticipationItem item, Date processingDate);
}
