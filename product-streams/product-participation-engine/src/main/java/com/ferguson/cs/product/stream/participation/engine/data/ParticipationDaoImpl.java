package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Repository
public class ParticipationDaoImpl implements ParticipationDao {
	private ParticipationMapper participationMapper;

	public ParticipationDaoImpl(ParticipationMapper participationMapper) {
		this.participationMapper = participationMapper;
	}

	@Override
	public ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId) {
		return participationMapper.getNextParticipationPendingActivation(processingDate, minParticipationId);
	}

	@Override
	public Boolean getParticipationIsActive(int participationId) {
		return participationMapper.getParticipationIsActive(participationId);
	}

	@Override
	public int setParticipationIsActive(int participationId, Boolean isActive) {
		return participationMapper.setParticipationIsActive(participationId, isActive);
	}

	@Override
	public int updateOwnerChangesForActivation(int participationId) {
		return participationMapper.updateOwnerChangesForActivation(participationId);
	}

	/**
	 * Set participationProduct.isOwner to 1 for rows matching the participationId.
	 * For uniqueIds in P, set participationProduct.isOwner to 1 if P has the highest priority on
	 * the product else 0.
	 */
	@Override
	public int addProductOwnershipForNewOwners(int participationId) {
		return participationMapper.addProductOwnershipForNewOwners();
	}

	/**
	 * For participationProduct rows where uniqueId in P and participationId != P.id,
	 * set participationProduct.isOwner = 0
	 */
	@Override
	public int removeProductOwnershipForOldOwners(int participationId) {
		return participationMapper.removeProductOwnershipForOldOwners();
	}

	@Override
	public int updateProductSaleIds(int participationId) {
		return participationMapper.updateProductSaleIds();
	}

	@Override
	public int updateLastOnSaleBasePrices(Date processingDate) {
		// Update existing or insert new lastOnSale rows with PriceBook_Cost basePrice values.
		return participationMapper.updateExistingLastOnSaleBasePrices(processingDate)
				+ participationMapper.insertMissingLastOnSaleBasePrices(processingDate);
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
		return participationMapper.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
	}

	@Override
	public int applyNewCalculatedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes) {
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any calculated discounts to pricebook prices.
		return participationMapper.applyNewCalculatedDiscounts(processingDate, userId, coolOffPeriodMinutes);
	}

	@Override
	public int updateProductModifiedDates(Date processingDate, int userId) {
		return participationMapper.updateProductModifiedDates(processingDate, userId);
	}

	@Override
	public ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId) {
		return participationMapper.getNextExpiredParticipation(processingDate, minParticipationId);
	}

	@Override
	public int updateOwnerChangesForDeactivation(int participationId) {
		return participationMapper.updateOwnerChangesForDeactivation(participationId);
	}

	@Override
	public int deleteParticipationV1Data(int participationId) {
		return participationMapper.deleteParticipationProducts(participationId)
		+ participationMapper.deleteParticipationCalculatedDiscounts(participationId);
	}

	@Override
	public int deleteParticipationItemPartial(int participationId) {
		return participationMapper.deleteParticipationItemPartial(participationId);
	}

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	@Override
	public int syncToCurrentPriorityParticipation() {
		return participationMapper.syncToCurrentPriorityParticipation();
	}

	@Override
	public int upsertParticipationItemPartial(ParticipationItemPartial itemPartial) {
		return participationMapper.upsertParticipationItemPartial(itemPartial);
	}

	@Override
	public int upsertParticipationProducts(int participationId, List<Integer> uniqueIds) {
		String csvUniqueIds = StringUtils.collectionToCommaDelimitedString(uniqueIds);
		int rowsAffected = participationMapper.deleteParticipationProducts(participationId);
		return rowsAffected + participationMapper.insertParticipationProducts(participationId, csvUniqueIds);
	}

	/**
	 * Upsert p22 and p1 calculated discounts for the Participation. Removes any existing calculated
	 * discount records, then inserts any calculated discount records.
	 */
	@Override
	public int upsertParticipationCalculatedDiscounts(
			int participationId,
			List<ParticipationCalculatedDiscount> calculatedDiscounts
	) {
		int rowsAffected = participationMapper.deleteCalculatedDiscountsOfParticipation(participationId);
		if(!calculatedDiscounts.isEmpty()) {
			rowsAffected += participationMapper.insertParticipationCalculatedDiscounts(calculatedDiscounts);
		}
		return rowsAffected;
	}
}
