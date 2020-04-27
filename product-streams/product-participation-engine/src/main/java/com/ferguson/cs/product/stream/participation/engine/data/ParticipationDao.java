package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

public interface ParticipationDao {

	// ACTIVATION / DEACTIVATION

	/**
	 * Mark a participation as active. This is done as part of the activation process.
	 * Will be active until deactivated.
	 * @return The number of records modified.
	 */
	int setParticipationIsActive(Integer participationId, Boolean isActive);

	/**
	 * Determine if a participation is currently active.
	 * @param participationId The id of a participation.
	 * @return Returns true if a participation is currently active, else false.
	 */
	boolean getParticipationIsActive(Integer participationId);

	// ACTIVATION

	/**
	 * Get next participation that is pending activation at the given date.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 */
	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId);

	/**
	 * Create the participationOwnerChanges temp table and fill it with the ownership
	 * changes caused by activating the specified participation.
	 * @param participationId The id of the activating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForActivation(Integer participationId);

	/**
	 * Update which uniqueIds are now owned by the activating participation.
	 * @param participationId The id of the activating or deactivating participation.
	 * @return The number of records modified.
	 */
	int addProductOwnershipForNewOwners(Integer participationId);

	/**
	 * Set isOwner to false for uniqueIds that are now owned by another participation.
	 * This is not needed for a deactivating participation since all the records that would be
	 * updated will be deleted.
	 * @param participationId The id of the activating participation.
	 * @return The number of records modified.
	 */
	int removeProductOwnershipForOldOwners(Integer participationId);

	/**
	 * Update saleIds for the products owned by the activating participation.
	 *
	 * @param participationId The id of the activating or deactivating participation.
	 * @return The number of records modified.
	 */
	int updateProductSaleIds(Integer participationId);

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
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(Integer userId);

	/**
	 * Apply calculated discounts to products becoming owned by a Participation.
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int applyNewCalculatedDiscounts(Date processingDate, Integer userId, Integer coolOffPeriod);

	/**
	 * Update the modified date for any product that was modified, to trigger product storage update.
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int updateProductModifiedDates(Date processingDate, Integer userId);


	// DEACTIVATION

	/**
	 * Get next participation that is expired and may be pending deactivation, at the given date.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 */
	ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId);

	/**
	 * Create the participationOwnerChanges temp table and fill it with the ownership
	 * changes caused by deactivating the specified participation.
	 * @param participationId The id of the deactivating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForDeactivation(Integer participationId);


	// MANUAL UNPUBLISH

	/**
	 * Delete participation data from the database. Assumes that the participation is not active.
	 * Does not delete the Participation record from Construct.
	 * @param participationId The id of the participation to delete from SQL.
	 * @return The number of records modified.
	 */
	int deleteParticipation(Integer participationId);

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	int syncToCurrentPriorityParticipation();
}
