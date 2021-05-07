package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

/**
 * This is responsible for common Participation SQL database queries. Any methods that are specific to certain
 * Participation types (e.g. participation@1) have been moved to daos named for those types.
 */
public interface ParticipationCoreDao {

	/**
	 * Mark a participation as active. This is done as part of the activation process.
	 * Will be active until deactivated.
	 * @return The number of records modified.
	 */
	int setParticipationIsActive(int participationId, Boolean isActive);

	/**
	 * Determine if a participation is currently active.
	 * @param participationId The id of a participation.
	 * @return Returns true if a participation is currently active, else false.
	 */
	Boolean getParticipationIsActive(int participationId);

	/**
	 * Get next participation that is pending activation at the given date.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 */
	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId);

	/**
	 * get participation partial record
	 * @return
	 */
	ParticipationItemPartial getParticipationItemPartial(int participationId);

	/**
	 * Create the participationOwnerChange temp table and fill it with the ownership
	 * changes caused by activating the specified participation.
	 * @param participationId The id of the activating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForActivation(int participationId);

	/**
	 * Update which uniqueIds are now owned by the activating participation.
	 * @param participationId The id of the activating or deactivating participation.
	 * @return The number of records modified.
	 */
	int addProductOwnershipForNewOwners(int participationId);

	/**
	 * Set isOwner to false for uniqueIds that are now owned by another participation.
	 * This is not needed for a deactivating participation since all the records that would be
	 * updated will be deleted.
	 * @param participationId The id of the activating participation.
	 * @return The number of records modified.
	 */
	int removeProductOwnershipForOldOwners(int participationId);

	/**
	 * Update saleIds for products becoming newly-owned or unowned.
	 * @return The number of records modified.
	 */
	int activateAndDeactivateProductSaleIds();

	/**
	 * Update the modified date for any product that was modified, to trigger product storage update.
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int updateProductModifiedDates(Date processingDate, int userId);

	/**
	 * Update pricing data to track base- and Was-prices for the product, to use to carry those values to the next
	 * discount when gap in time between the ending discount and the new discount is within a configured amount (the
	 * "Cool off period").
	 * @param processingDate The date the participation is being processed.
	 * @return The number of records updated or inserted.
	 */
	int updateLastOnSaleForDeactivatingProducts(Date processingDate);

	/**
	 * Get next participation that is expired and may be pending deactivation, at the given date.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 */
	ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId);

	/**
	 * Create the participationOwnerChange temp table and fill it with the ownership
	 * changes caused by deactivating the specified participation.
	 * @param participationId The id of the deactivating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForDeactivation(int participationId);

	/**
	 * Delete participationProduct rows for the given Participation.
	 * @param participationId The id of the participation from which to delete products.
	 * @return The number of records modified.
	 */
	int deleteParticipationProducts(int participationId);

	/**
	 * Delete the partial participation record. Non-type specific.
	 * @param participationId The participationId of the participationItemPartial record to delete.
	 * @return The number of records modified.
	 */
	int deleteParticipationItemPartial(int participationId);

	/**
	 * Add or update a participation Item.
	 * @param itemPartial The participation to add or update.
	 * @return The number of records modified.
	 */
	int upsertParticipationItemPartial(ParticipationItemPartial itemPartial);

	/**
	 * Add or update the products for a participation.
	 * @param participationId The id of the participation to which the products belong.
	 * @param uniqueIds The list of product variant ids that belong to the participation.
	 * @return The number of records modified.
	 */
	int upsertParticipationProducts(int participationId, List<Integer> uniqueIds);


	// HISTORY

	/**
	 * store core Participation state to log table upon publication
	 */
	int insertParticipationItemPartialHistory(ParticipationItemPartial itemPartial);

	/**
	 * associate products to participation log
	 */
	void insertParticipationProductsHistory(int partialHistoryId, List<Integer> uniqueIds);

	/**
	 * record date of current participation version activation for posterity
	 */
	int updateActivatedHistory(int participationId, Date processingDate);

	/**
	 * record date of current participation version deactivation for posterity
	 */
	int updateDeactivatedHistory(int participationId, Date processingDate);
}
