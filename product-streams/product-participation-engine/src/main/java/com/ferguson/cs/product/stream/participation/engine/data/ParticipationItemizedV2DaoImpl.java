package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

@Repository
public class ParticipationItemizedV2DaoImpl implements ParticipationItemizedV2Dao {
	private ParticipationItemizedV2Mapper participationItemizedV2Mapper;

	public ParticipationItemizedV2DaoImpl(ParticipationItemizedV2Mapper participationItemizedV2Mapper) {
		this.participationItemizedV2Mapper = participationItemizedV2Mapper;
	}

	@Override
	public int applyNewItemizedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes) {
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any itemized discounted prices to pricebook_cost.cost.
		return participationItemizedV2Mapper.applyNewItemizedDiscounts(processingDate, userId, coolOffPeriodMinutes);
	}

	@Override
	public int updateLastOnSaleBasePrices(Date processingDate) {
		// Update existing or insert new lastOnSale rows with PriceBook_Cost basePrice values.
		return participationItemizedV2Mapper.updateExistingLastOnSaleBasePrices(processingDate)
				+ participationItemizedV2Mapper.insertMissingLastOnSaleBasePrices(processingDate);
	}

	@Override
	public int takePricesOffSaleAndApplyPendingBasePriceUpdates(int userId) {
		// Apply any pending base price updates to the prices being overridden by the new owning participation.
		// Updates the PriceBook_Cost basePrice column with the basePrice from latestBasePrice if different.
		// Set the cost column to the (possibly new) basePrice. This takes the pricebook price off sale.
		// Notes:
		// They need to be taken off sale in case the new owning participation does not have price discounts.
		// The new participation will overwrite these changes if it has a discount, so a possible optimization would
		// be to only apply pending base price changes if needed, i.e. if the new participation does not have a discount...
		return participationItemizedV2Mapper.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
	}

	@Override
	public int deleteParticipationItemizedDiscounts(int participationId) {
		return participationItemizedV2Mapper.deleteParticipationItemizedDiscounts(participationId);
	}

	@Override
	public int upsertParticipationItemizedDiscounts(List<ParticipationItemizedDiscount> participationItemizedDiscounts) {
		int participationId = participationItemizedDiscounts.get(0).getParticipationId();
		int rowsAffected = participationItemizedV2Mapper.deleteParticipationItemizedDiscounts(participationId);
		String csDiscountedPricesRows = participationItemizedDiscounts.stream()
				.map(discountedPricesRow -> discountedPricesRow.getUniqueId() + ","
						+ discountedPricesRow.getPricebookId() + ","
						+ discountedPricesRow.getPrice()
				).collect(Collectors.joining("\n"));
		rowsAffected += participationItemizedV2Mapper.insertParticipationItemizedDiscounts(participationId,
					csDiscountedPricesRows);
		return rowsAffected;
	}

	// HISTORY

	@Override
	public void insertParticipationItemizedDiscountsHistory(
			int partialHistoryId, List<ParticipationItemizedDiscount> itemizedDiscounts) {
		String csDiscounts = itemizedDiscounts.stream()
				.map(discountedPricesRow -> discountedPricesRow.getUniqueId() + ","
						+ discountedPricesRow.getPricebookId() + ","
						+ discountedPricesRow.getPrice()
				).collect(Collectors.joining("\n"));
		participationItemizedV2Mapper.insertParticipationItemizedDiscountsHistory
					(partialHistoryId, csDiscounts);
	}

}
