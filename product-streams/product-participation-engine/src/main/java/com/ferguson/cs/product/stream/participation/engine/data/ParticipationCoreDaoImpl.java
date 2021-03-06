package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationContentType;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Repository
public class ParticipationCoreDaoImpl implements ParticipationCoreDao {
	private final static int[] CONTENT_TYPE_IDS_AFFECTING_PRICES = {
			ParticipationContentType.PARTICIPATION_V1.contentTypeId(),
			ParticipationContentType.PARTICIPATION_V2.contentTypeId(),
			ParticipationContentType.PARTICIPATION_ITEMIZED_V1.contentTypeId(),
			ParticipationContentType.PARTICIPATION_ITEMIZED_V2.contentTypeId()
	};

	private final ParticipationCoreMapper participationCoreMapper;

	public ParticipationCoreDaoImpl(ParticipationCoreMapper participationCoreMapper) {
		this.participationCoreMapper = participationCoreMapper;
	}

	@Override
	public ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate, Integer minParticipationId) {
		return participationCoreMapper.getNextParticipationPendingActivation(processingDate, minParticipationId);
	}

	@Override
	public Boolean getParticipationIsActive(int participationId) {
		return participationCoreMapper.getParticipationIsActive(participationId);
	}

	@Override
	public int setParticipationIsActive(int participationId, Boolean isActive) {
		return participationCoreMapper.setParticipationIsActive(participationId, isActive);
	}


	@Override
	public ParticipationItemPartial getParticipationItemPartial(int participationId) {
		return participationCoreMapper.getParticipationItemPartial(participationId);
	}

	@Override
	public int updateOwnerChangesForActivation(int participationId) {
		return participationCoreMapper.updateOwnerChangesForActivation(participationId);
	}

	/**
	 * Set participationProduct.isOwner to 1 for rows matching the participationId.
	 * For uniqueIds in P, set participationProduct.isOwner to 1 if P has the highest priority on
	 * the product else 0.
	 */
	@Override
	public int addProductOwnershipForNewOwners(int participationId) {
		return participationCoreMapper.addProductOwnershipForNewOwners();
	}

	/**
	 * For participationProduct rows where uniqueId in P and participationId != P.id,
	 * set participationProduct.isOwner = 0
	 */
	@Override
	public int removeProductOwnershipForOldOwners(int participationId) {
		return participationCoreMapper.removeProductOwnershipForOldOwners();
	}

	@Override
	public int activateAndDeactivateProductSaleIds() {
		return participationCoreMapper.activateAndDeactivateProductSaleIds();
	}

	@Override
	public int updateProductModifiedDates(Date processingDate, int userId) {
		return participationCoreMapper.updateProductModifiedDates(processingDate, userId);
	}

	@Override
	public int updateLastOnSaleForDeactivatingProducts(Date processingDate) {
		// Update existing or insert new lastOnSale rows with PriceBook_Cost values.
		return participationCoreMapper.updateLastOnSaleForDeactivatingProducts(CONTENT_TYPE_IDS_AFFECTING_PRICES,
				processingDate) + participationCoreMapper.insertMissingLastOnSaleForDeactivatingProducts(
				CONTENT_TYPE_IDS_AFFECTING_PRICES, processingDate);
	}

	@Override
	public ParticipationItemPartial getNextExpiredParticipation(Date processingDate, Integer minParticipationId) {
		return participationCoreMapper.getNextExpiredParticipation(processingDate, minParticipationId);
	}

	@Override
	public int updateOwnerChangesForDeactivation(int participationId) {
		return participationCoreMapper.updateOwnerChangesForDeactivation(participationId);
	}

	@Override
	public int deleteParticipationProducts(int participationId) {
		return participationCoreMapper.deleteParticipationProducts(participationId);
	}


	@Override
	public int deleteParticipationItemPartial(int participationId) {
		return participationCoreMapper.deleteParticipationItemPartial(participationId);
	}

	@Override
	public int upsertParticipationItemPartial(ParticipationItemPartial itemPartial) {
		return participationCoreMapper.upsertParticipationItemPartial(itemPartial);
	}

	@Override
	public int upsertParticipationProducts(int participationId, List<Integer> uniqueIds) {
		int rowsAffected = participationCoreMapper.deleteParticipationProducts(participationId);
		if (!uniqueIds.isEmpty()) {
			String csvUniqueIds = StringUtils.collectionToCommaDelimitedString(uniqueIds);
			rowsAffected += participationCoreMapper.insertParticipationProducts(participationId, csvUniqueIds);
		}
		return rowsAffected;
	}

	// HISTORY

	@Override
	public int insertParticipationItemPartialHistory(ParticipationItemPartial itemPartial) {
		int nextVersionId =
				participationCoreMapper.getHighestParticipationHistoryVersionId(itemPartial.getParticipationId()) +1;
		return participationCoreMapper.insertParticipationItemPartialHistory(itemPartial, nextVersionId);
	}

	@Override
	public void insertParticipationProductsHistory(int partialHistoryId, List<Integer> uniqueIds) { ;
		if (!uniqueIds.isEmpty()) {
			String csvUniqueIds = StringUtils.collectionToCommaDelimitedString(uniqueIds);
			participationCoreMapper.insertParticipationProductsHistory(partialHistoryId, csvUniqueIds);
		}
	}

	@Override
	public int updateActivatedHistory(int participationId, Date processingDate) {
		return participationCoreMapper.updateActivatedHistory(participationId, processingDate);
	}

	@Override
	public int updateDeactivatedHistory(int participationId, Date processingDate) {
		int rowsAffected = participationCoreMapper.updateDeactivatedHistory(participationId, processingDate);
		// reset expiration counter start date on all versions of this participation so that they expire together
		participationCoreMapper.updateWoodchipperDates(participationId, processingDate);
		return rowsAffected;
	}
}
