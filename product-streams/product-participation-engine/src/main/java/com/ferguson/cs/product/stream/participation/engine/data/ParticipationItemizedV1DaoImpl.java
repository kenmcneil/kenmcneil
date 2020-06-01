package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

@Repository
public class ParticipationItemizedV1DaoImpl implements ParticipationItemizedV1Dao {
	private ParticipationItemizedV1Mapper participationItemizedV1Mapper;

	public ParticipationItemizedV1DaoImpl(ParticipationItemizedV1Mapper participationItemizedV1Mapper) {
		this.participationItemizedV1Mapper = participationItemizedV1Mapper;
	}

	@Override
	public int applyNewItemizedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes) {
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any itemized discounted prices to pricebook_cost.cost.
		return participationItemizedV1Mapper.applyNewItemizedDiscounts(processingDate, userId, coolOffPeriodMinutes);
	}

	@Override
	public int updateLastOnSaleBasePrices(Date processingDate) {
		// Update existing or insert new lastOnSale rows with PriceBook_Cost basePrice values.
		return participationItemizedV1Mapper.updateExistingLastOnSaleBasePrices(processingDate)
				+ participationItemizedV1Mapper.insertMissingLastOnSaleBasePrices(processingDate);
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
		return participationItemizedV1Mapper.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
	}

	@Override
	public int deleteParticipationItemizedDiscounts(int participationId) {
		return participationItemizedV1Mapper.deleteParticipationItemizedDiscounts(participationId);
	}

	@Override
	public int upsertParticipationItemizedDiscounts(List<ParticipationItemizedDiscount> participationItemizedDiscounts) {
		int participationId = participationItemizedDiscounts.get(0).getParticipationId();
		int rowsAffected = participationItemizedV1Mapper.deleteParticipationItemizedDiscounts(participationId);
		String csDiscountedPricesRows = participationItemizedDiscounts.stream()
				.map(discountedPricesRow -> discountedPricesRow.getUniqueId() + ","
						+ discountedPricesRow.getPricebookId() + ","
						+ discountedPricesRow.getPrice()
				).collect(Collectors.joining("\n"));
		rowsAffected += participationItemizedV1Mapper.insertParticipationItemizedDiscounts(participationId,
					csDiscountedPricesRows);
		return rowsAffected;
	}

}
