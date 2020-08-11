package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;

/**
 * This is responsible for SQL database queries specific to participation@1, eg calculated discount methods.
 */
public interface ParticipationV1Dao {

	/**
	 * Apply calculated discounts to products becoming owned by a Participation.
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @param coolOffPeriodMinutes a grace period for small edits (eg 15 minute) during which wasPrice should not be
	 * affected.
	 * @return The number of records modified.
	 */
	int applyNewCalculatedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes);

	/**
	 * Record last-on-sale base prices.
	 * @param processingDate The date the participation is being processed.
	 * @return The number of records modified.
	 */
	int updateLastOnSaleBasePrices(Date processingDate);

	/**
	 * Take the prices owned by the participation off sale.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(int userId);

	/**
	 * Delete participationCalculatedDiscount rows for the given Participation.
	 * @param participationId The id of the participation from which to delete calculated discounts.
	 * @return The number of records modified.
	 */
	int deleteParticipationCalculatedDiscounts(int participationId);

	/**
	 * Upsert calculated discounts for the Participation. Removes any existing calculated
	 * discount records, then inserts any calculated discount records.
	 */
	int upsertParticipationCalculatedDiscounts(
			int participationId, List<ParticipationCalculatedDiscount> calculatedDiscounts);


	// HISTORY

	/**
	 *
	 */
	void insertParticipationCalculatedDiscountsHistory(
			int partialHistoryId, List<ParticipationCalculatedDiscount> calculatedDiscounts);
}
