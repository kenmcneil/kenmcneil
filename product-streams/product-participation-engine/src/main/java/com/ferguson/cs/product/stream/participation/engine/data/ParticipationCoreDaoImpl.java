package com.ferguson.cs.product.stream.participation.engine.data;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Repository
public class ParticipationCoreDaoImpl implements ParticipationCoreDao {
	private ParticipationCoreMapper participationCoreMapper;
	private ParticipationV1Dao participationV1Dao;
	private ParticipationItemizedV1Dao participationItemizedV1Dao;

	public ParticipationCoreDaoImpl(ParticipationCoreMapper participationCoreMapper,
									ParticipationV1Dao participationV1Dao,
									ParticipationItemizedV1Dao participationItemizedV1Dao) {
		this.participationCoreMapper = participationCoreMapper;
		this.participationV1Dao = participationV1Dao;
		this.participationItemizedV1Dao = participationItemizedV1Dao;
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
	public int activateProductSaleIds() {
		return participationCoreMapper.activateProductSaleIds();
	}

	@Override
	public int deactivateProductSaleIds() {
		return participationCoreMapper.deactivateProductSaleIds();
	}

	@Override
	public int updateProductModifiedDates(Date processingDate, int userId) {
		return participationCoreMapper.updateProductModifiedDates(processingDate, userId);
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
}
