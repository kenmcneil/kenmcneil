package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationCalculatedDiscount;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Repository
public class ParticipationV1DaoImpl implements ParticipationV1Dao {
	private ParticipationV1Mapper participationV1Mapper;

	public ParticipationV1DaoImpl(ParticipationV1Mapper participationV1Mapper) {
		this.participationV1Mapper = participationV1Mapper;
	}

//Core Discount Methods

	@Override
	public ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId) {
		return participationV1Mapper.getNextParticipationPendingActivation(processingDate, minParticipationId);
	}

	@Override
	public Boolean getParticipationIsActive(int participationId) {
		return participationV1Mapper.getParticipationIsActive(participationId);
	}

	@Override
	public int setParticipationIsActive(int participationId, Boolean isActive) {
		return participationV1Mapper.setParticipationIsActive(participationId, isActive);
	}

	@Override
	public int updateOwnerChangesForActivation(int participationId) {
		return participationV1Mapper.updateOwnerChangesForActivation(participationId);
	}

	/**
	 * Set participationProduct.isOwner to 1 for rows matching the participationId.
	 * For uniqueIds in P, set participationProduct.isOwner to 1 if P has the highest priority on
	 * the product else 0.
	 */
	@Override
	public int addProductOwnershipForNewOwners(int participationId) {
		return participationV1Mapper.addProductOwnershipForNewOwners();
	}

	/**
	 * For participationProduct rows where uniqueId in P and participationId != P.id,
	 * set participationProduct.isOwner = 0
	 */
	@Override
	public int removeProductOwnershipForOldOwners(int participationId) {
		return participationV1Mapper.removeProductOwnershipForOldOwners();
	}

	@Override
	public int activateProductSaleIds() {
		return participationV1Mapper.activateProductSaleIds();
	}

	@Override
	public int deactivateProductSaleIds() {
		return participationV1Mapper.deactivateProductSaleIds();
	}

	@Override
	public int updateProductModifiedDates(Date processingDate, int userId) {
		return participationV1Mapper.updateProductModifiedDates(processingDate, userId);
	}

	@Override
	public ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId) {
		return participationV1Mapper.getNextExpiredParticipation(processingDate, minParticipationId);
	}

	@Override
	public int updateOwnerChangesForDeactivation(int participationId) {
		return participationV1Mapper.updateOwnerChangesForDeactivation(participationId);
	}

	@Override
	public int deleteParticipationProducts(int participationId) {
		return participationV1Mapper.deleteParticipationProducts(participationId);
	}


	@Override
	public int deleteParticipationItemPartial(int participationId) {
		return participationV1Mapper.deleteParticipationItemPartial(participationId);
	}

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	@Override
	public int syncToCurrentPriorityParticipation() {
		return participationV1Mapper.syncToCurrentPriorityParticipation();
	}

	@Override
	public int upsertParticipationItemPartial(ParticipationItemPartial itemPartial) {
		return participationV1Mapper.upsertParticipationItemPartial(itemPartial);
	}

	@Override
	public int upsertParticipationProducts(int participationId, List<Integer> uniqueIds) {
		int rowsAffected = participationV1Mapper.deleteParticipationProducts(participationId);
		if (!uniqueIds.isEmpty()) {
			String csvUniqueIds = StringUtils.collectionToCommaDelimitedString(uniqueIds);
			rowsAffected += participationV1Mapper.insertParticipationProducts(participationId, csvUniqueIds);
		}
		return rowsAffected;
	}

//Calculated Discounts Methods

	@Override
	public int applyNewCalculatedDiscounts(Date processingDate, int userId, long coolOffPeriodMinutes) {
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any calculated discounts to pricebook prices.
		return participationV1Mapper.applyNewCalculatedDiscounts(processingDate, userId, coolOffPeriodMinutes);
	}

	@Override
	public int updateLastOnSaleBasePrices(Date processingDate) {
		// Update existing or insert new lastOnSale rows with PriceBook_Cost basePrice values.
		return participationV1Mapper.updateExistingLastOnSaleBasePrices(processingDate)
				+ participationV1Mapper.insertMissingLastOnSaleBasePrices(processingDate);
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
		return participationV1Mapper.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
	}

	@Override
	public int deleteParticipationCalculatedDiscounts(int participationId) {
		return participationV1Mapper.deleteParticipationCalculatedDiscounts(participationId);
	}

	@Override
	public int upsertParticipationCalculatedDiscounts(int participationId,
														List<ParticipationCalculatedDiscount> calculatedDiscounts) {
		int rowsAffected = participationV1Mapper.deleteParticipationCalculatedDiscounts(participationId);
		if(!calculatedDiscounts.isEmpty()) {
			rowsAffected += participationV1Mapper.insertParticipationCalculatedDiscounts(calculatedDiscounts);
		}
		return rowsAffected;
	}
}
