package com.ferguson.cs.product.stream.participation.engine.sql;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface ParticipationMapper {

	/**
	 * Returns true if the isActive column is 1, else false. True indicates that
	 * the participation is currently activated.
	 * @param participationId
	 * @return The isActive status of the participation, or null if participation not found.
	 */
	Boolean getParticipationIsActive(@Param("participationId") Integer participationId);

	int setParticipationIsActive(@Param("participationId") Integer participationId, @Param("isActive") Boolean isActive);

	/**
	 * Truncate the temporary owner changes table and fill it with the ownership changes caused by deactivating
	 * the specified participation.
	 * @param participationId
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForDeactivation(@Param("participationId") Integer participationId);

	/**
	 * Truncate the temporary owner changes table and fill it with the ownership changes caused by activating
	 * the specified participation.
	 * @param participationId
	 * @return The number of records modified.
	 */
	int updateOwnerChangesForActivation(@Param("participationId") Integer participationId);

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
	 * @param processingDate
	 * @return The number of records modified.
	 */
	int updateExistingLastOnSaleBasePrices(@Param("processingDate") Date processingDate);

	/**
	 * Inserts missing lastOnSale records from the PriceBook_Cost table. Use when the price is going off-sale.
	 * Use after updating any existing records with updateExistingLastOnSaleBasePrices().
	 *
	 * @param processingDate
	 * @return The number of records modified.
	 */
	int insertMissingLastOnSaleBasePrices(@Param("processingDate") Date processingDate);

	/**
	 * Take the prices owned by the participation off sale.
	 * Update PriceBook_Cost basePrice from pending baseprice, and update the cost column
	 * from the new basePrice.
	 *
	 * @param userId
	 * @return The number of records modified.
	 */
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(@Param("userId") Integer userId);

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
	 * @param processingDate
	 * @param userId
	 * @return The number of records modified.
	 */
	int applyNewCalculatedDiscounts(@Param("processingDate") Date processingDate, @Param("userId") Integer userId);

	/**
	 * Update product.modified to trigger product cache update.
	 *
	 * @param processingDate
	 * @param userId
	 * @return The number of records modified.
	 */
	int updateProductModifiedDates(@Param("processingDate") Date processingDate, @Param("userId") Integer userId);

	/**
	 * Remove all records from the participationProduct table for the given
	 * participation id. Must be done ahead of deleting from participationItemPartial
	 * table to avoid FK_constraint failure.
	 *
	 * @param participationId
	 * @return
	 */
	int deleteParticipationProductsByParticipationId(@Param("participationId") Integer participationId);

	/**
	 * Delete the calculated discount records for the given participation id.
	 *
	 * @param participationId
	 */
	int deleteParticipationCalculatedDiscountsByParticipationId(@Param("participationId") Integer participationId);

	/**
	 * Delete the participationItemPartial record for given participation id.
	 *
	 * @param participationId
	 */
	int deleteParticipationItemPartialByParticipationId(@Param("participationId") Integer participationId);

	// TODO remove currentPriorityParticipation code
	int syncToCurrentPriorityParticipation();
}
