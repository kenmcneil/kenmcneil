package com.ferguson.cs.product.stream.participation.engine.test.ingredients;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.test.model.ParticipationScenarioIngredient;

/**
 * Verify behavior when activating then deactivating with no schedule, no products, and no effects,
 * to verify basic activation/deactivation works.
 */
public class ActivationDeactivationIngredient implements ParticipationScenarioIngredient {

	/**
	 *
	 */
	@Override
	public void beforeUnpublish(ParticipationItem item, Date runDate) {
		// verify data is correct in sql, given the original fixture values and the current run date

	}

	@Override
	public void afterUnpublish(ParticipationItem item, Date runDate) {
		// verify data is correct in sql, given the original fixture values and the current run date

	}
}
