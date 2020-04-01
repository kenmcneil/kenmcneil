package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.ferguson.cs.product.stream.participation.engine.sql.ParticipationDao;

@Service
public class ParticipationServiceImpl implements ParticipationService {

	private static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);

	private ParticipationEngineSettings participationEngineSettings;
	private ParticipationDao participationDao;
	private ConstructService constructService;

	@Autowired
	public void setParticipationEngineSettings(ParticipationEngineSettings participationEngineSettings) {
		this.participationEngineSettings = participationEngineSettings;
	}

	@Autowired
	public void setParticipationDao(ParticipationDao participationDao) {
		this.participationDao = participationDao;
	}

	@Autowired
	public void setConstructService(ConstructService constructService) {
		this.constructService = constructService;
	}

	@Override
	public void processPendingActivations() {
		boolean isTestMode = participationEngineSettings.getTestModeEnabled();
		int previousParticipationId = 0;

		// activate each pending participation
		ParticipationItem item = constructService.getNextPendingActivationParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Got same participation as last time, status update failed???");
				break;
			}
			previousParticipationId = item.getId();

			if (isTestMode) {
				LOG.debug(toJson(item));
			}

			Date processingDate = new Date();
			activateParticipation(item, processingDate);
			constructService.updateParticipationItemStatus(
					item.getId(),
					ParticipationItemStatus.PUBLISHED,
					ParticipationItemUpdateStatus.NEEDS_CLEANUP,
					processingDate
			);
			LOG.info(item.getId() + ": participation activated");

			item = constructService.getNextPendingActivationParticipation();
		}
	}

	@Override
	public void processPendingDeactivations() {
		boolean isTestMode = participationEngineSettings.getTestModeEnabled();
		int previousParticipationId = 0;

		// activate each pending participation
		ParticipationItem item = constructService.getNextPendingDeactivationParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Got same participation as last time, status update failed???");
				break;
			}
			previousParticipationId = item.getId();

			if (isTestMode) {
				LOG.debug(toJson(item));
			}

			Date processingDate = new Date();
			deactivateParticipation(item, processingDate);
			constructService.updateParticipationItemStatus(
					item.getId(),
					ParticipationItemStatus.ARCHIVED,
					null,
					processingDate
			);
			LOG.info(item.getId() + ": participation deactivated and set to archived status");

			item = constructService.getNextPendingDeactivationParticipation();
		}
	}

	@Override
	public void processPendingUnpublishes() {
		boolean isTestMode = participationEngineSettings.getTestModeEnabled();
		int previousParticipationId = 0;

		// activate each pending participation
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Got same participation as last time, status update failed???");
				break;
			}
			previousParticipationId = item.getId();

			if (isTestMode) {
				LOG.debug(toJson(item));
			}

			Date processingDate = new Date();
			unpublishParticipation(item, processingDate);
			constructService.updateParticipationItemStatus(
					item.getId(),
					ParticipationItemStatus.DRAFT,
					null,
					processingDate
			);
			LOG.info(item.getId() + ": participation unpublished and set to draft status");

			item = constructService.getNextPendingUnpublishParticipation();
		}
	}

	@Transactional
	protected void activateParticipation(ParticipationItem item, Date processingDate) {
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

	@Transactional
	protected  void deactivateParticipation(ParticipationItem item, Date processingDate) {
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

	@Transactional
	protected void unpublishParticipation(ParticipationItem item, Date processingDate) {
		int participationId = item.getId();

		if (BooleanUtils.isTrue(participationDao.getParticipationIsActive(participationId))) {
			// deactivate, which also deletes the participation records
			deactivateParticipation(item, processingDate);
		} else {
			// only delete the participation records
			int rowsAffected = participationDao.deleteParticipation(participationId);
			LOG.info(participationId + ": " + rowsAffected + " rows removed to delete participation");
		}
	}

	private String toJson(Object item) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
		} catch (Exception e) {
			// ignore
		}
		return "";
	}
}
