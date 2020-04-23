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
	private final ParticipationEngineSettings participationEngineSettings;

	ParticipationServiceImpl(ParticipationDao participationDao, ParticipationEngineSettings participationEngineSettings) {
		this.participationDao = participationDao;
		this.participationEngineSettings = participationEngineSettings;
	}

	@Transactional
	@Override
	public void activateParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();
		int userId = item.getLastModifiedUserId();
		int totalRows = 0;
		int coolOffPeriod = participationEngineSettings.getCoolOffPeriod();
		int rowsAffected = participationDao.setParticipationIsActive(participationId, true);
		totalRows += rowsAffected;
		LOG.debug("==== activating participation {} ====", participationId);

		// determine what products are changing ownership and store into temp table
		rowsAffected = participationDao.updateOwnerChangesForActivation(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: updating {} products for activation", participationId, rowsAffected);

		rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);

		rowsAffected = participationDao.removeProductOwnershipForOldOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products dis-owned from other participations", participationId, rowsAffected);

		rowsAffected = participationDao.updateProductSaleIds(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);

		// activate new discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId, coolOffPeriod);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices put on sale from calculated discounts", participationId, rowsAffected);

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		// TODO remove currentPriorityParticipation code (see SODEV-25037)
		participationDao.syncToCurrentPriorityParticipation();

		LOG.debug("{}: {} total rows updated to activate", participationId, totalRows);
	}

	@Transactional
	@Override
	public void deactivateParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();
		int userId = item.getLastModifiedUserId();
		int coolOffPeriod = participationEngineSettings.getCoolOffPeriod();
		int totalRows = 0;

		int rowsAffected = participationDao.setParticipationIsActive(participationId, false);
		totalRows += rowsAffected;
		LOG.debug("==== deactivating participation {} ====", participationId);

		// determine what products are changing ownership and store into temp table
		rowsAffected = participationDao.updateOwnerChangesForDeactivation(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: updating {} products for deactivation", participationId, rowsAffected);

		// assign ownership of each unique id to any active fallback participations,
		// but don't bother to disown from deactivating participation since it will be deleted at the end
		rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);

		// update sale ids to fallback participations or to zeros
		rowsAffected = participationDao.updateProductSaleIds(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product sale ids updated", participationId, rowsAffected);

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		totalRows += rowsAffected;
		LOG.debug("{}: {} lastOnSale basePrice values saved", participationId, rowsAffected);

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices taken off sale from calculated discounts", participationId, rowsAffected);

		// activate fallback discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId, coolOffPeriod);
		totalRows += rowsAffected;
		LOG.debug("{}: {} prices put on sale from calculated discounts", participationId, rowsAffected);

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		// remove all records for this participation
		rowsAffected = participationDao.deleteParticipation(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} rows removed to delete participation", participationId, rowsAffected);

		// TODO remove currentPriorityParticipation code (see SODEV-25037)
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