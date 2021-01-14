package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParticipationItemizedV2Mapper {

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
	int takePricesOffSaleAndApplyPendingBasePriceUpdates(int userId);

	/**
	 * Sets pricebook_Cost.cost (pbcost) to a discounted base price using the participationItemizedDiscount
	 * (discounts) table.
	 * Also updates basePrice with last-on-sale base price if present.
	 * Uses updated base price
	 * @return
	 */
	int applyNewItemizedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes);

	/**
	 * Insert all itemized discounts for a participation
	 * @param csDiscounts , a multiline string in the form "uniqueid,pricebookId,discountedPrice\n"
	 * @return the number of records inserted
	 */
	int insertParticipationItemizedDiscounts(int participationId, String csDiscounts);

	/**
	 * Delete all itemized discounts for a participation
	 * @return the number of records deleted
	 */
	int deleteParticipationItemizedDiscounts(int participationId);

	/**
	 * store state of published discount and relate it to published participation version in history
	 */
	void insertParticipationItemizedDiscountsHistory(
			int partialHistoryId, String csDiscounts);
}
