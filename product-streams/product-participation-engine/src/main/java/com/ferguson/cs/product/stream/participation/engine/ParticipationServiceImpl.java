package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

@Service
public class ParticipationServiceImpl implements ParticipationService {

	private final static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);

	private final ParticipationDao participationDao;
	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationLifecycleService participationLifecycleService;

	public ParticipationServiceImpl(
			ParticipationDao participationDao,
			ParticipationEngineSettings participationEngineSettings,
			ParticipationLifecycleService participationLifecycleService
	) {
		this.participationDao = participationDao;
		this.participationEngineSettings = participationEngineSettings;
		this.participationLifecycleService = participationLifecycleService;
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
	public Boolean getParticipationIsActive(Integer participationId) {
		return participationDao.getParticipationIsActive(participationId);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void publishParticipation(ParticipationItem item, Date processingDate) {
		participationLifecycleService.getLifecycleFor(participationLifecycleService.getContentType(item))
				.publish(item, processingDate);
	}

	/**
	 * Run (1) set-up queries common to all content types including calculating new product ownership
	 * for overlaps, (2) type-specific activation/deactivation queries for override/fallback,
	 * and (3) run common finish-up queries.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void activateParticipation(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId() == null
				? participationEngineSettings.getTaskUserId()
				: itemPartial.getLastModifiedUserId();

		LOG.debug("==== activating participation {} ====", participationId);

		int totalRows = participationDao.setParticipationIsActive(participationId, true);

		// determine what products are changing ownership and store into temp table
		// -- not logging returned row-modified count since it's not always accurate
		participationDao.updateOwnerChangesForActivation(participationId);

		int rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products with new participation ownership", participationId, rowsAffected);

		rowsAffected = participationDao.removeProductOwnershipForOldOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products dis-owned from other participations", participationId, rowsAffected);

		// Run type-specific activation queries.
		totalRows += participationLifecycleService.activateByType(itemPartial, processingDate);

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		LOG.debug("{}: {} total rows updated to activate", participationId, totalRows);
	}

	/**
	 * Run (1) set-up queries common to all content types including calculating new product ownership
	 * for overlaps, (2) type-specific activation/deactivation queries for override/fallback,
	 * and (3) run common finish-up queries.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void deactivateParticipation(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();
		int userId = itemPartial.getLastModifiedUserId() == null
				? participationEngineSettings.getTaskUserId()
				: itemPartial.getLastModifiedUserId();

		LOG.debug("==== deactivating participation {} ====", participationId);

		int totalRows = participationDao.setParticipationIsActive(participationId, false);

		// determine what products are changing ownership and store into temp table
		participationDao.updateOwnerChangesForDeactivation(participationId);

		// assign ownership of each unique id to any active fallback participations,
		// but don't bother to disown from deactivating participation since it will be deleted at the end
		int rowsAffected = participationDao.addProductOwnershipForNewOwners(participationId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} products with new participation owxnership", participationId, rowsAffected);

		// Run type-specific deactivation queries.
		totalRows += participationLifecycleService.deactivateByType(itemPartial, processingDate);

		// update modified date on each product modified
		rowsAffected = participationDao.updateProductModifiedDates(processingDate, userId);
		totalRows += rowsAffected;
		LOG.debug("{}: {} product modified dates updated", participationId, rowsAffected);

		LOG.debug("{}: {} total rows updated to deactivate", participationId, totalRows);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void unpublishParticipation(ParticipationItemPartial itemPartial, Date processingDate) {
		int participationId = itemPartial.getParticipationId();

		// Run type-specific unpublish queries for the Participation being unpublished. The unpublish handler
		// must delete any records added in publish().
		int rowsAffected = participationLifecycleService.getLifecycleFor(itemPartial.getContentType())
				.unpublish(itemPartial, processingDate);

		rowsAffected += participationDao.deleteParticipationItemPartial(participationId);

		LOG.debug("{}: {} total rows deleted to unpublish participation", participationId, rowsAffected);
	}

	// TODO remove currentPriorityParticipation code (see SODEV-25037)
	@Transactional
	@Override
	public int syncToCurrentPriorityParticipation() {
		return participationDao.syncToCurrentPriorityParticipation();
	}
}
