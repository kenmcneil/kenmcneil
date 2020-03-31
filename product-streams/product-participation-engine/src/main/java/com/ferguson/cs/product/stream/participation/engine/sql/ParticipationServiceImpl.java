package com.ferguson.cs.product.stream.participation.engine.sql;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

@Service
public class ParticipationServiceImpl implements ParticipationService {

	private static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);

	@Autowired
	ParticipationDao participationDao;

	public void activateParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();
		int userId = item.getLastModifiedUserId();
		int totalRows = 0;

		int rowsAffected = participationDao.setParticipationIsActive(participationId, true);
		totalRows += rowsAffected;
		LOG.info("==== activating participation " + participationId + " ====");

		// determine what products are changing ownership and store into temp table
		rowsAffected = participationDao.updateOwnerChangesForActivation(participationId);
		totalRows += rowsAffected;
		LOG.info(participationId + ": updating " + rowsAffected + " products for activation");

		rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		LOG.info(participationId + ": " + rowsAffected + " products with new participation ownership");
		totalRows += rowsAffected;

		rowsAffected = participationDao.removeProductOwnershipForOldOwners(participationId);
		LOG.info(participationId + ": " + rowsAffected + " products dis-owned from other participations");
		totalRows += rowsAffected;

		rowsAffected = participationDao.updateProductSaleIds(participationId);
		LOG.info(participationId + ": " + rowsAffected + " product sale ids updated");
		totalRows += rowsAffected;

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		LOG.info(participationId + ": " + rowsAffected + " lastOnSale basePrice values saved");
		totalRows += rowsAffected;

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		LOG.info(participationId + ": " + rowsAffected + " prices taken off sale from calculated discounts");
		totalRows += rowsAffected;

		// activate new discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId);
		LOG.info(participationId + ": " + rowsAffected + " prices put on sale from calculated discounts");
		totalRows += rowsAffected;

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		LOG.info(participationId + ": " + rowsAffected + " product modified dates updated");
		totalRows += rowsAffected;

		LOG.info(participationId + ": " + totalRows + " total rows updated to activate");
	}

	@Override
	public void deactivateParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();
		int userId = item.getLastModifiedUserId();
		int totalRows = 0;

		int rowsAffected = participationDao.setParticipationIsActive(participationId, false);
		LOG.info("==== deactivating participation " + participationId + " ====");
		totalRows += rowsAffected;

		// determine what products are changing ownership and store into temp table
		rowsAffected = participationDao.updateOwnerChangesForDeactivation(participationId);
		LOG.info(participationId + ": updating " + rowsAffected + " products for deactivation");
		totalRows += rowsAffected;

		// assign ownership of each unique id to any active fallback participations,
		// but don't bother to disown from deactivating participation since it will be deleted at the end
		rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		LOG.info(participationId + ": " + rowsAffected + " products with new participation ownership");
		totalRows += rowsAffected;

		// update sale ids to fallback participations or to zeros
		rowsAffected = participationDao.updateProductSaleIds(participationId);
		LOG.info(participationId + ": " + rowsAffected + " product sale ids updated");
		totalRows += rowsAffected;

		rowsAffected = participationDao.updateLastOnSaleBasePrices(processingDate);
		LOG.info(participationId + ": " + rowsAffected + " lastOnSale basePrice values saved");
		totalRows += rowsAffected;

		rowsAffected = participationDao.takePricesOffSaleAndApplyPendingBasePriceUpdates(userId);
		LOG.info(participationId + ": " + rowsAffected + " prices taken off sale from calculated discounts");
		totalRows += rowsAffected;

		// activate fallback discounts (if any)
		rowsAffected = participationDao.applyNewCalculatedDiscounts(processingDate, userId);
		LOG.info(participationId + ": " + rowsAffected + " prices put on sale from calculated discounts");
		totalRows += rowsAffected;

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		LOG.info(participationId + ": " + rowsAffected + " product modified dates updated");
		totalRows += rowsAffected;

		// remove all records for this participation
		rowsAffected = participationDao.deleteParticipation(participationId);
		LOG.info(participationId + ": " + rowsAffected + " rows removed to delete participation");
		totalRows += rowsAffected;

		LOG.info(participationId + ": " + totalRows + " total rows updated to deactivate");
	}

	@Override
	public void unpublishParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();

		if (BooleanUtils.isTrue(participationDao.getParticipationIsActive(participationId))) {
			// deactivate, which also deletes the participation records
			deactivateParticipation(item, processingDate);
		} else {
			// only delete the participation records
			int rowsAffected = participationDao.deleteParticipation(participationId);
			LOG.info(participationId + ": " + rowsAffected + " rows removed to delete and unpublish participation");
		}
	}
}
