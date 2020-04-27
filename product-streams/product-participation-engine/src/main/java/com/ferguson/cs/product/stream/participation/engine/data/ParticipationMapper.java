package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Mapper
public interface ParticipationMapper {

	/**
	 * Determine if a participation is currently active.
	 * @param participationId The id of a participation.
	 * @return Returns true if a participation is currently active, else false.
	 */
	boolean getParticipationIsActive(Integer participationId);

	/**
	 * Mark a participation as active or inactive.
	 * @param participationId The id of a participation.
	 * @param isActive Use true to indicate active, false to indicate inactive.
	 * @return The number of records modified.
	 */
	int setParticipationIsActive(Integer participationId, Boolean isActive);

	/**
	 * Create the participationOwnerChanges temp table and fill it with the ownership
	 * changes caused by deactivating the specified participation.
	 * @param participationId The id of the deactivating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForDeactivation(Integer participationId);

	/**
	 * Create the participationOwnerChanges temp table and fill it with the ownership
	 * changes caused by activating the specified participation.
	 * @param participationId The id of the activating participation.
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForActivation(Integer participationId);

	/**
	 * Set ownership for products in P, only considering active participations.
	 * If P is active then it is considered for ownership otherwise ownership will go to
	 * other (if any) participations.
	 * @return The number of records modified.
	 */
	int addProductOwnershipForNewOwners();

	/**
	 * Set ownership of each uniqueId in the participation to other participations (if any).
	 * Does not change the isOwner value for the participation, because this is needed in
	 * subsequent queries to have the list of uniqueIds that are being updated.
	 * @return The number of records modified.
	 */
	int removeProductOwnershipForOldOwners();

	/**
	 * Set product sale ids to fallback participations or to zero if no fallback, based on tempOwnerChanges values.
	 * @return The number of records modified.
	 */
	int updateProductSaleIds();

	/**
	 * Updates lastOnSale records from the PriceBook_Cost table. Use when the price is going off-sale.
	 *
	 * @param processingDate The date the participation is being processed.
	 * @return The number of records modified.
	 */
	int updateExistingLastOnSaleBasePrices(Date processingDate);

	/**
	 * Inserts missing lastOnSale records from the PriceBook_Cost table. Use when the price is going off-sale.
	 * Use after updating any existing records with updateExistingLastOnSaleBasePrices().
	 *
	 * @param processingDate The date the participation is being processed.
	 * @return The number of records modified.
	 */
	int insertMissingLastOnSaleBasePrices(Date processingDate);

	/**
	 * Take the prices owned by the participation off sale.
	 * Update PriceBook_Cost basePrice from pending baseprice, and update the cost column
	 * from the new basePrice.
	 *
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(Integer userId);

	/**
	 * Sets priceBook_Cost.cost (pbcost) to a discounted base price
	 * using the participationCalculatedDiscount (discounts) table.
	 *
	 * Also updates basePrice with last-on-sale base price if present.
	 * Uses updated base price to calculate new sale price.
	 *
	 * A discounted base price is calculated as follows:
	 * if percent discount:
	 *      pbcost.cost = pbcost.basePrice * discounts.changeValue
	 * else if dollar amount discount:
	 *      pbcost.cost = pbocost.basePrice + discounts.changeValue
	 *
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int applyNewCalculatedDiscounts(Date processingDate, Integer userId, Integer coolOffPeriod);

	/**
	 * Update product.modified to trigger product cache update.
	 *
	 * @param processingDate The date the participation is being processed.
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int updateProductModifiedDates(Date processingDate, Integer userId);

	/**
	 * Remove all records from the participationProduct table for the given
	 * participation id. Must be done ahead of deleting from participationItemPartial
	 * table to avoid FK_constraint failure.
	 *
	 * @param participationId The id of the participation to delete from SQL.
	 * @return The number of records modified.
	 */
	int deleteParticipationProductsByParticipationId(Integer participationId);

	/**
	 * Delete the calculated discount records for the given participation id.
	 *
	 * @param participationId The id of the participation to delete from SQL.
	 * @return The number of records modified.
	 */
	int deleteParticipationCalculatedDiscountsByParticipationId(Integer participationId);

	/**
	 * Delete the participationItemPartial record for given participation id.
	 *
	 * @param participationId The id of the participation to delete from SQL.
	 * @return The number of records modified.
	 */
	int deleteParticipationItemPartialByParticipationId(Integer participationId);

	/**
	 * Get next participation that is pending activation at the given date.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 */
	ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId);

	/**
	 * Get next participation that is expired at the given date.
	 * Returns Participation records that are expired whether active or not.
	 * Optionally restrict to records with id >= minParticipationId (for testmode).
	 */
	ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId);

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	int syncToCurrentPriorityParticipation();
}
