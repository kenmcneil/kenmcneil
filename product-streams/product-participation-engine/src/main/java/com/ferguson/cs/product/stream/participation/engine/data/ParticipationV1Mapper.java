package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;

@Mapper
public interface ParticipationV1Mapper {

	/**
	 * Take the prices owned by the participation off sale.
	 * Update PriceBook_Cost basePrice from pending baseprice, and update the cost column
	 * from the new basePrice.
	 *
	 * @param userId The id of the user initiating the changes.
	 * @return The number of records modified.
	 */
	int takeV1PricesOffSaleAndApplyPendingBasePriceUpdates(int userId);

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
	int applyNewV1CalculatedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes);

	int insertParticipationV1CalculatedDiscounts(@Param("calculatedDiscounts") List<ParticipationCalculatedDiscount> calculatedDiscounts);

	/**
	 * Delete the calculated discount records for the given participation id.
	 *
	 * @param participationId The id of the participation from which to delete calculated discounts.
	 * @return The number of records modified.
	 */
	int deleteParticipationV1CalculatedDiscounts(int participationId);

	/**
	 * store state of published discount and relate it to published participation version in history
	 */
	int insertParticipationV1CalculatedDiscountsHistory(
			int partialHistoryId, List<ParticipationCalculatedDiscount> calculatedDiscounts);
}
