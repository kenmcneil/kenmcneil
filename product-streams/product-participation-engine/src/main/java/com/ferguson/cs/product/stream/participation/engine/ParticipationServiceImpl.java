package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Service
public class ParticipationServiceImpl implements ParticipationService {

	private final static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);

	private final ParticipationDao participationDao;
	private final ParticipationEngineSettings participationEngineSettings;

	public ParticipationServiceImpl(ParticipationDao participationDao, ParticipationEngineSettings participationEngineSettings) {
		this.participationDao = participationDao;
		this.participationEngineSettings = participationEngineSettings;
	}

	@Override
	public ParticipationItemPartial getNextParticipationPendingActivation(Date processingDate) {
		return participationDao.getNextParticipationPendingActivation(
				processingDate, participationEngineSettings.getTestModeMinParticipationId());
	}

	@Override
	public ParticipationItemPartial getNextExpiredParticipation(Date processingDate) {
		return participationDao.getNextExpiredParticipation(
				processingDate, participationEngineSettings.getTestModeMinParticipationId());
	}

	@Override
	public boolean getParticipationIsActive(Integer participationId) {
		return participationDao.getParticipationIsActive(participationId);
	}

	@Override
	public void activateParticipation(ParticipationItemPartial item, Date processingDate) {
		int participationId = item.getParticipationId();
		int userId = item.getLastModifiedUserId();
		int coolOffPeriod = participationEngineSettings.getCoolOffPeriod();
		int totalRows = 0;

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
		rowsAffected = participationDao.syncToCurrentPriorityParticipation();
		totalRows += rowsAffected;
		LOG.debug("{}: {} rows updated for currentPriorityParticipation sync", participationId, rowsAffected);

		LOG.debug("{}: {} total rows updated to activate", participationId, totalRows);
	}

	@Override
	public void deactivateParticipation(ParticipationItemPartial item, Date processingDate) {
		int participationId = item.getParticipationId();
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

		LOG.debug("{}: {} total rows updated to deactivate", participationId, totalRows);
	}

	@Override
	public void unpublishParticipation(ParticipationItemPartial item, Date processingDate) {
		int totalRows = 0;
		int participationId = item.getParticipationId();
		int rowsAffected = participationDao.deleteParticipation(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} rows removed to delete participation", participationId, rowsAffected);

		// TODO remove currentPriorityParticipation code (see SODEV-25037)
		rowsAffected = participationDao.syncToCurrentPriorityParticipation();
		totalRows += rowsAffected;
		LOG.debug("{}: {} rows updated for currentPriorityParticipation sync", participationId, rowsAffected);

		LOG.debug("{}: {} total rows updated to unpublish", participationId, totalRows);
	}
}
