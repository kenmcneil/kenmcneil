package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemizedDiscount;

@Repository
public class ParticipationItemizedV1V2DaoImpl implements ParticipationItemizedV1V2Dao {
	final private ParticipationItemizedV1V2Mapper participationItemizedV1V2Mapper;

	public ParticipationItemizedV1V2DaoImpl(ParticipationItemizedV1V2Mapper participationItemizedV1V2Mapper) {
		this.participationItemizedV1V2Mapper = participationItemizedV1V2Mapper;
	}

	@Override
	public int applyNewItemizedDiscounts(int contentTypeId, Date processingDate, int userId, long coolOffPeriodMinutes) {
		Assert.isTrue(contentTypeId == ParticipationContentType.PARTICIPATION_ITEMIZED_V1.contentTypeId()
						|| contentTypeId == ParticipationContentType.PARTICIPATION_ITEMIZED_V2.contentTypeId(),
				"must be an itemized v1 or v2 type");
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any itemized discounted prices to pricebook_cost.cost.
		return participationItemizedV1V2Mapper.applyNewItemizedV1V2Discounts(contentTypeId, processingDate, userId, coolOffPeriodMinutes);
	}

	@Override
	public int takePricesOffSaleAndApplyPendingBasePriceUpdates(int contentTypeId, int userId) {
		Assert.isTrue(contentTypeId == ParticipationContentType.PARTICIPATION_ITEMIZED_V1.contentTypeId()
						|| contentTypeId == ParticipationContentType.PARTICIPATION_ITEMIZED_V2.contentTypeId(),
				"must be an itemized v1 or v2 type");
		// Apply any pending base price updates to the prices being overridden by the new owning participation.
		// Updates the PriceBook_Cost basePrice column with the basePrice from latestBasePrice if different.
		// Set the cost column to the (possibly new) basePrice. This takes the pricebook price off sale.
		// Notes:
		// They need to be taken off sale in case the new owning participation does not have price discounts.
		// The new participation will overwrite these changes if it has a discount, so a possible optimization would
		// be to only apply pending base price changes if needed, i.e. if the new participation does not have a discount...
		return participationItemizedV1V2Mapper.takeItemizedV1V2PricesOffSaleAndApplyPendingBasePriceUpdates(contentTypeId, userId);
	}

	@Override
	public int deleteParticipationItemizedDiscounts(int participationId) {
		return participationItemizedV1V2Mapper.deleteParticipationItemizedV1V2Discounts(participationId);
	}

	@Override
	public int upsertParticipationItemizedDiscounts(List<ParticipationItemizedDiscount> participationItemizedDiscounts) {
		int participationId = participationItemizedDiscounts.get(0).getParticipationId();
		int rowsAffected = participationItemizedV1V2Mapper.deleteParticipationItemizedV1V2Discounts(participationId);
		String csDiscountedPricesRows = participationItemizedDiscounts.stream()
				.map(discountedPricesRow -> discountedPricesRow.getUniqueId() + ","
						+ discountedPricesRow.getPricebookId() + ","
						+ discountedPricesRow.getPrice()
				).collect(Collectors.joining("\n"));
		rowsAffected += participationItemizedV1V2Mapper.insertParticipationItemizedV1V2Discounts(participationId,
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
		participationItemizedV1V2Mapper.insertParticipationItemizedV1V2DiscountsHistory
					(partialHistoryId, csDiscounts);
	}

}
