package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;

@Repository
public class ParticipationV2DaoImpl implements ParticipationV2Dao {
	final private ParticipationV2Mapper participationV2Mapper;

	public ParticipationV2DaoImpl(ParticipationV2Mapper participationV2Mapper) {
		this.participationV2Mapper = participationV2Mapper;
	}

	@Override
	public int applyNewCalculatedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes) {
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any calculated discounts to pricebook prices.
		return participationV2Mapper.applyNewV2CalculatedDiscounts(processingDate, userId, coolOffPeriodMinutes);
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
		return participationV2Mapper.takeV2PricesOffSaleAndApplyPendingBasePriceUpdates(userId);
	}

	@Override
	public int deleteParticipationCalculatedDiscounts(int participationId) {
		return participationV2Mapper.deleteParticipationV2CalculatedDiscounts(participationId);
	}

	@Override
	public int upsertParticipationCalculatedDiscounts(int participationId,
			List<ParticipationCalculatedDiscount> calculatedDiscounts) {
		int rowsAffected = participationV2Mapper.deleteParticipationV2CalculatedDiscounts(participationId);
		if (!calculatedDiscounts.isEmpty()) {
			rowsAffected += participationV2Mapper.insertParticipationV2CalculatedDiscounts(calculatedDiscounts);
		}
		return rowsAffected;
	}

	// HISTORY

	@Override
	public void insertParticipationCalculatedDiscountsHistory(
			int partialHistoryId, List<ParticipationCalculatedDiscount> calculatedDiscounts) {
		if (!calculatedDiscounts.isEmpty()) {
			participationV2Mapper.insertParticipationV2CalculatedDiscountsHistory(partialHistoryId, calculatedDiscounts);
		}
	}
}
