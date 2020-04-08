package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;

import org.springframework.stereotype.Repository;

@Repository
public class ParticipationDaoImpl implements ParticipationDao {
	private ParticipationMapper participationMapper;

	ParticipationDaoImpl(ParticipationMapper participationMapper) {
		this.participationMapper = participationMapper;
	}

	@Override
	public Boolean getParticipationIsActive(Integer participationId) {
		return participationMapper.getParticipationIsActive(participationId);
	}

	@Override
	public int setParticipationIsActive(Integer participationId, Boolean isActive) {
		return participationMapper.setParticipationIsActive(participationId, isActive);
	}

	@Override
	public int updateOwnerChangesForActivation(Integer participationId) {
		return participationMapper.updateOwnerChangesForActivation(participationId);
	}

	/**
	 * Set participationProduct.isOwner to 1 for rows matching the participationId.
	 * For uniqueIds in P, set participationProduct.isOwner to 1 if P has the highest priority on
	 * the product else 0.
	 */
	@Override
	public int addProductOwnershipForNewOwners(Integer participationId) {
		return participationMapper.addProductOwnershipForNewOwners();
	}

	/**
	 * For participationProduct rows where uniqueId in P and participationId != P.id,
	 * set participationProduct.isOwner = 0
	 */
	@Override
	public int removeProductOwnershipForOldOwners(Integer participationId) {
		return participationMapper.removeProductOwnershipForOldOwners();
	}

	@Override
	public int updateProductSaleIds(Integer participationId) {
		return participationMapper.updateProductSaleIds();
	}

	@Override
	public int updateLastOnSaleBasePrices(Date processingDate) {
		// Update existing or insert new lastOnSale rows with PriceBook_Cost basePrice values.
		return participationMapper.updateExistingLastOnSaleBasePrices(processingDate)
				+ participationMapper.insertMissingLastOnSaleBasePrices(processingDate);
	}

	@Override
	public int takePricesOffSaleAndApplyPendingBasePriceUpdates(Integer userId) {
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
	public int applyNewCalculatedDiscounts(Date processingDate, Integer userId) {
		// Restore any base prices that were on sale recently enough to be considered back-to-back,
		// and apply any calculated discounts to pricebook prices.
		return participationMapper.applyNewCalculatedDiscounts(processingDate, userId);
	}

	@Override
	public int updateProductModifiedDates(Date processingDate, Integer userId) {
		return participationMapper.updateProductModifiedDates(processingDate, userId);
	}


	// DEACTIVATION

	@Override
	public int updateOwnerChangesForDeactivation(Integer participationId) {
		return participationMapper.updateOwnerChangesForDeactivation(participationId);
	}

	@Override
	public int deleteParticipation(Integer participationId) {
		return participationMapper.deleteParticipationProductsByParticipationId(participationId)
		+ participationMapper.deleteParticipationCalculatedDiscountsByParticipationId(participationId)
		+ participationMapper.deleteParticipationItemPartialByParticipationId(participationId);
	}

	// TODO remove currentPriorityParticipation code
	@Override
	public int syncToCurrentPriorityParticipation() {
		return participationMapper.syncToCurrentPriorityParticipation();
	}
}