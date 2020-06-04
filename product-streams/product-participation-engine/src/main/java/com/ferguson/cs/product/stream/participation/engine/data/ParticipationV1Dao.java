package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

/**
 * This is responsible for SQL database queries specific to participation@1, eg calculated discount methods.
 */
public interface ParticipationV1Dao {

////Core Discount Methods
//
//	/**
//	 * Mark a participation as active. This is done as part of the activation process.
//	 * Will be active until deactivated.
//	 * @return The number of records modified.
//	 */
//	int setParticipationIsActive(int participationId, Boolean isActive);
//
//	/**
//	 * Determine if a participation is currently active.
//	 * @param participationId The id of a participation.
//	 * @return Returns true if a participation is currently active, else false.
//	 */
//	Boolean getParticipationIsActive(int participationId);
//
//	/**
//	 * Get next participation that is pending activation at the given date.
//	 * Optionally restrict to records with id >= minParticipationId (for testmode).
//	 */
//	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId);
//
//	/**
//	 * get participation partial record
//	 * @param participationId
//	 * @return
//	 */
//	ParticipationItemPartial getParticipationItemPartial(int participationId);
//
//	/**
//	 * Create the participationOwnerChange temp table and fill it with the ownership
//	 * changes caused by activating the specified participation.
//	 * @param participationId The id of the activating participation.
//	 * @return The number of records modified.
//	 */
//	int updateOwnerChangesForActivation(int participationId);
//
//	/**
//	 * Update which uniqueIds are now owned by the activating participation.
//	 * @param participationId The id of the activating or deactivating participation.
//	 * @return The number of records modified.
//	 */
//	int addProductOwnershipForNewOwners(int participationId);
//
//	/**
//	 * Set isOwner to false for uniqueIds that are now owned by another participation.
//	 * This is not needed for a deactivating participation since all the records that would be
//	 * updated will be deleted.
//	 * @param participationId The id of the activating participation.
//	 * @return The number of records modified.
//	 */
//	int removeProductOwnershipForOldOwners(int participationId);
//
//	/**
//	 * Update saleIds for products becoming newly-owned.
//	 * @return The number of records modified.
//	 */
//	int activateProductSaleIds();
//
//	/**
//	 * Set saleIds for products becoming un-owned to 0.
//	 * @return The number of records modified.
//	 */
//	int deactivateProductSaleIds();
//
//	/**
//	 * Update the modified date for any product that was modified, to trigger product storage update.
//	 * @param processingDate The date the participation is being processed.
//	 * @param userId The id of the user initiating the changes.
//	 * @return The number of records modified.
//	 */
//	int updateProductModifiedDates(Date processingDate, int userId);
//
//	/**
//	 * Get next participation that is expired and may be pending deactivation, at the given date.
//	 * Optionally restrict to records with id >= minParticipationId (for testmode).
//	 */
//	ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId);
//
//	/**
//	 * Create the participationOwnerChange temp table and fill it with the ownership
//	 * changes caused by deactivating the specified participation.
//	 * @param participationId The id of the deactivating participation.
//	 * @return The number of records modified.
//	 */
//	int updateOwnerChangesForDeactivation(int participationId);
//
//	/**
//	 * Delete participationProduct rows for the given Participation.
//	 * @param participationId The id of the participation from which to delete products.
//	 * @return The number of records modified.
//	 */
//	int deleteParticipationProducts(int participationId);
//
//	/**
//	 * Delete the partial participation record. Non-type specific.
//	 * @param participationId The participationId of the participationItemPartial record to delete.
//	 * @return The number of records modified.
//	 */
//	int deleteParticipationItemPartial(int participationId);
//
//	int upsertParticipationItemPartial(ParticipationItemPartial itemPartial);
//
//	int upsertParticipationProducts(int participationId, List<Integer> uniqueIds);
//
////Calculated Discount Methods

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
			int participationId,
			List<ParticipationCalculatedDiscount> calculatedDiscounts
	);
}
