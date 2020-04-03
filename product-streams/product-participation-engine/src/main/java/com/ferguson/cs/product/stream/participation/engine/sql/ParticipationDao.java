package com.ferguson.cs.product.stream.participation.engine.sql;

import java.util.Date;

public interface ParticipationDao {

	// ACTIVATION / DEACTIVATION

	/**
	 * Mark a participation as active. This is done as part of the activation process.
	 * Will be active until deactivated.
	 * @param participationId
	 * @param isActive
	 * @return
	 */
	int setParticipationIsActive(Integer participationId, Boolean isActive);

	/**
	 * Return true if a participation is currently active, else false.
	 * @param participationId
	 * @return
	 */
	Boolean getParticipationIsActive(Integer participationId);

	// ACTIVATION

	/**
	 * Truncate the tempOwnerChanges table and fill it with the ownership changes caused by activating
	 * the specified participation.
	 * @param participationId
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForActivation(Integer participationId);

	/**
	 * Update which uniqueIds are now owned by the activating participation.
	 * @param participationId id of the activating participation
	 * @return The number of records modified.
	 */
	int addProductOwnershipForNewOwners(Integer participationId);

	/**
	 * Set isOwner to false for uniqueIds that are now owned by another participation.
	 * @param participationId
	 * @return The number of records modified.
	 */
	int removeProductOwnershipForOldOwners(Integer participationId);

	/**
	 * Update saleIds for the products owned by the activating participation.
	 *
	 * @param participationId
	 * @return The number of records modified.
	 */
	int updateProductSaleIds(Integer participationId);

	/**
	 * Record last-on-sale base prices.
	 * @param processingDate
	 * @return
	 */
	int updateLastOnSaleBasePrices(Date processingDate);

	/**
	 * Take the prices owned by the participation off sale.
	 * @param userId
	 * @return
	 */
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(Integer userId);

	/**
	 * Apply calculated discounts to products becoming owned by a Participation.
	 * @param processingDate
	 * @param userId
	 * @return
	 */
	int applyNewCalculatedDiscounts(Date processingDate, Integer userId);

	/**
	 * Update the modified date for any product that was modified, to trigger product storage update.
	 * @param processingDate
	 * @param userId
	 * @return
	 */
	int updateProductModifiedDates(Date processingDate, Integer userId);


	// DEACTIVATION

	/**
	 * Truncate the tempOwnerChanges table and fill it with the ownership changes caused by activating
	 * the specified participation.
	 * @param participationId
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForDeactivation(Integer participationId);


	// MANUAL UNPUBLISH

	/**
	 * Delete participation data from the database. Assumes that the participation is not active.
	 *
	 * @param participationId
	 * @return The number of records modified.
	 */
	int deleteParticipation(Integer participationId);

	//

	// TODO remove currentPriorityParticipation code
	int syncToCurrentPriorityParticipation();
}
