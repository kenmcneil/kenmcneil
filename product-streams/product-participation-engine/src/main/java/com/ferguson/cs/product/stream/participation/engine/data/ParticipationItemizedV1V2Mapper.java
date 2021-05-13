package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParticipationItemizedV1V2Mapper {

	/**
	 * Take the prices owned by the participation off sale.
	 * Update PriceBook_Cost basePrice from pending baseprice, and update the cost column
	 * from the new basePrice.
	 *
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int takeItemizedV1V2PricesOffSaleAndApplyPendingBasePriceUpdates(int contentTypeId, int userId);

	/**
	 * For each activating product unique id, restore values if present from the last-on-sale table to
	 * the pricebook_cost record, and set pricebook_Cost.cost to the specified price from the
	 * participationItemizedDiscount table.
	 * @return The number of records modified.
	 */
	int applyNewItemizedV1V2Discounts(int contentTypeId, Date processingDate, int userId, long coolOffPeriodMinutes);

	/**
	 * Insert all itemized discounts for a participation
	 * @param csDiscounts , a multiline string in the form "uniqueid,pricebookId,discountedPrice\n"
	 * @return the number of records inserted
	 */
	int insertParticipationItemizedV1V2Discounts(int participationId, String csDiscounts);

	/**
	 * Delete all itemized discounts for a participation
	 * @return the number of records deleted
	 */
	int deleteParticipationItemizedV1V2Discounts(int participationId);

	/**
	 * store state of published discount and relate it to published participation version in history
	 */
	void insertParticipationItemizedV1V2DiscountsHistory(
			int partialHistoryId, String csDiscounts);
}
