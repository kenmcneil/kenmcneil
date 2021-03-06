package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

/**
 * This is responsible for SQL database queries specific to participation-itemized@1, eg itemized discount methods.
 */
public interface ParticipationItemizedV1V2Dao {

	/**
	 * Apply itemized discounted prices to products becoming owned by a Participation.
	 * @param contentTypeId The id of the type of the participation (must be either itemized v1 or v2).
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @param coolOffPeriodMinutes a grace period (eg 15 minutes) for small edits during which wasPrice should not be
	 * affected
	 * @return The number of records modified.
	 */
	int applyNewItemizedDiscounts(int contentTypeId, Date processingDate, int userId, long coolOffPeriodMinutes);

	/**
	 * Take the prices owned by the participation off sale. Only the products owned by the given type of
	 * itemized participation are processed.
	 * @param contentTypeId The id of the type of the participation (must be either itemized v1 or v2).
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(int contentTypeId, int userId);

	/**
	 * Delete participationItemizedDiscount rows for the given Participation.
	 * @param participationId The id of the participation from which to delete itemized discounts.
	 * @return The number of records modified.
	 */
	int deleteParticipationItemizedDiscounts(int participationId);

	/**
	 * Upserts itemized prices for a Participation where present. Removes existing itemized discounts for that
	 * participation first, then inserts any passed in.
	 */
	int upsertParticipationItemizedDiscounts(List<ParticipationItemizedDiscount> participationItemizedDiscounts);


	// HISTORY

	/**
	 * store state of published discount and relate it to published participation version in history
	 */
	void insertParticipationItemizedDiscountsHistory(
			int partialHistoryId, List<ParticipationItemizedDiscount> itemizedDiscounts);
}
