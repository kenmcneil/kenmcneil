package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

@Service
public class ParticipationServiceImpl implements ParticipationService {

	private final static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);

	private final ParticipationDao participationDao;

	ParticipationServiceImpl(ParticipationDao participationDao) {
		this.participationDao = participationDao;
	}

	@Transactional
	@Override
	public void activateParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();
		int userId = item.getLastModifiedUserId();
		int totalRows = 0;

		int rowsAffected = participationDao.setParticipationIsActive(participationId, true);
		totalRows += rowsAffected;
		LOG.debug("==== activating participation {} ====", participationId);

		// determine what products are changing ownership and store into temp table
		rowsAffected = participationDao.updateOwnerChangesForActivation(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: updating {} products for activation", participationId, rowsAffected);

		rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);
		totalRows += rowsAffected;

		rowsAffected = participationDao.removeProductOwnershipForOldOwners(participationId);
		LOG.debug("{}: {} products dis-owned from other participations", participationId, rowsAffected);
		totalRows += rowsAffected;

		rowsAffected = participationDao.updateProductSaleIds(participationId);
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);
		totalRows += rowsAffected;

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);
		totalRows += rowsAffected;

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);
		totalRows += rowsAffected;

		// activate new discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId);
		LOG.debug("{}: {} prices put on sale from calculated discounts", participationId, rowsAffected);
		totalRows += rowsAffected;

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);
		totalRows += rowsAffected;

		// TODO remove currentPriorityParticipation code
		participationDao.syncToCurrentPriorityParticipation();

		LOG.debug("{}: {} total rows updated to activate", participationId, totalRows);
	}

	@Transactional
	@Override
	public void deactivateParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();
		int userId = item.getLastModifiedUserId();
		int totalRows = 0;

		int rowsAffected = participationDao.setParticipationIsActive(participationId, false);
		LOG.debug("==== deactivating participation {} ====", participationId);
		totalRows += rowsAffected;

		// determine what products are changing ownership and store into temp table
		rowsAffected = participationDao.updateOwnerChangesForDeactivation(participationId);
		LOG.debug("{}: updating {} products for deactivation", participationId, rowsAffected);
		totalRows += rowsAffected;

		// assign ownership of each unique id to any active fallback participations,
		// but don't bother to disown from deactivating participation since it will be deleted at the end
		rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);
		totalRows += rowsAffected;

		// update sale ids to fallback participations or to zeros
		rowsAffected = participationDao.updateProductSaleIds(participationId);
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);
		totalRows += rowsAffected;

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);
		totalRows += rowsAffected;

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);
		totalRows += rowsAffected;

		// activate fallback discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId);
		LOG.debug("{}: {} prices put on sale from calculated discounts", participationId, rowsAffected);
		totalRows += rowsAffected;

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);
		totalRows += rowsAffected;

		// remove all records for this participation
		rowsAffected = participationDao.deleteParticipation(participationId);
		LOG.debug("{}: {} rows removed to delete participation", participationId, rowsAffected);
		totalRows += rowsAffected;

		// TODO remove currentPriorityParticipation code
		participationDao.syncToCurrentPriorityParticipation();

		LOG.debug("{}: {} total rows updated to deactivate", participationId, totalRows);
	}

	@Transactional
	@Override
	public void unpublishParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();

		if (BooleanUtils.isTrue(participationDao.getParticipationIsActive(participationId))) {
			// deactivate, which also deletes the participation records
			deactivateParticipation(item, processingDate);
		} else {
			// only delete the participation records
			int rowsAffected = participationDao.deleteParticipation(participationId);
			LOG.debug("{}: {} rows removed to delete participation", participationId, rowsAffected);
		}
	}
}
