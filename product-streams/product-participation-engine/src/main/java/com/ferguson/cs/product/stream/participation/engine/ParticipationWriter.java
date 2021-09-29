package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.construct.MetricsService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemStatus;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemUpdateStatus;
import com.newrelic.api.agent.Trace;

/**
 * The methods in the writer handle each of the four events: publish, unpublish, activate, and deactivate.
 * Each event handler is set to add a New Relic transaction, and saves the participation id with the transaction.
 * An exception is notified to New Relic, associated to the NR transaction in which it occurred.
 */
public class ParticipationWriter {
	private final ConstructService constructService;
	private final ParticipationLifecycleService participationLifecycleService;
	private final MetricsService metricsService;

	public ParticipationWriter(
			ConstructService constructService,
			MetricsService metricsService,
			ParticipationLifecycleService participationLifecycleService) {
		this.constructService = constructService;
		this.metricsService = metricsService;
		this.participationLifecycleService = participationLifecycleService;
	}

	/**
	 * Publish the given ParticipationItem by upserting its data into SQL tables.
	 * If this Participation is currently active then deactivates it first.
	 */
	@Trace(dispatcher=true, metricName="processPublish")
	@Transactional
	public void processPublish(ParticipationItem item, Date processingDate) {
		metricsService.addCustomParameter("participationId", item.getId());

		try {
			// If this Participation is currently active then deactivate it before publishing the new version.
			if (BooleanUtils.isTrue(participationLifecycleService.getParticipationIsActive(item.getId()))) {
				participationLifecycleService.deactivateByType(ParticipationItemPartial.builder()
						.participationId(item.getId())
						.lastModifiedUserId(item.getLastModifiedUserId())
						.build(), processingDate);
			}

			participationLifecycleService.publishByType(item, processingDate);
			constructService.updateParticipationItemStatus(item.getId(), ParticipationItemStatus.PUBLISHED,
					ParticipationItemUpdateStatus.NEEDS_UPDATE, processingDate, item.getLastModifiedUserId());
		} catch (Exception e) {
			RuntimeException exceptionWithMessage = new RuntimeException("Error publishing participation " + item.getId(), e);
			metricsService.noticeError(exceptionWithMessage);
			throw exceptionWithMessage;
		}
	}

	@Trace(dispatcher=true, metricName="processActivation")
	@Transactional
	public void processActivation(ParticipationItemPartial itemPartial, Date processingDate) {
		metricsService.addCustomParameter("participationId", itemPartial.getParticipationId());

		try {
			participationLifecycleService.activateByType(itemPartial, processingDate);
			constructService.updateParticipationItemStatus(itemPartial.getParticipationId(), ParticipationItemStatus.PUBLISHED,
					ParticipationItemUpdateStatus.NEEDS_CLEANUP, processingDate, itemPartial.getLastModifiedUserId());
		} catch (Exception e) {
			RuntimeException exceptionWithMessage = new RuntimeException(
					"Error publishing participation " + itemPartial.getParticipationId(), e);
			metricsService.noticeError(exceptionWithMessage);
			throw exceptionWithMessage;
		}
	}

	@Trace(dispatcher=true, metricName="processDeactivation")
	@Transactional
	public void processDeactivation(ParticipationItemPartial itemPartial, Date processingDate) {
		metricsService.addCustomParameter("participationId", itemPartial.getParticipationId());

		if (itemPartial.getIsActive()) {
			participationLifecycleService.deactivateByType(itemPartial, processingDate);
		}
		participationLifecycleService.unpublishByType(itemPartial, processingDate);
		constructService.updateParticipationItemStatus(
				itemPartial.getParticipationId(),
				ParticipationItemStatus.ARCHIVED,
				null,
				processingDate,
				itemPartial.getLastModifiedUserId()
		);
	}

	/**
	 * Deactivate given participation if needed and unpublish. If the record is not present in SQL then simply
	 * set Construct record to draft status.
	 */
	@Trace(dispatcher=true, metricName="processUnpublish")
	@Transactional
	public void processUnpublish(ParticipationItem item, Date processingDate) {
		metricsService.addCustomParameter("participationId", item.getId());

		// Get the participation item from SQL. If not there, then we'll skip any SQL changes and change the Construct
		// record to be DRAFT status (assumes the SQL Participation record is not present because the engine already
		// unpublished the record in SQL and the corresponding unpublish Construct update failed silently).
		ParticipationItemPartial itemPartial = participationLifecycleService.getParticipationItemPartial(item.getId());
		if (itemPartial != null) {
			// Update the user id in SQL to the latest from Construct.
			itemPartial.setLastModifiedUserId(item.getLastModifiedUserId());

			// If active then deactivate before unpublishing.
			if (itemPartial.getIsActive()) {
				participationLifecycleService.deactivateByType(itemPartial, processingDate);
			}

			participationLifecycleService.unpublishByType(itemPartial, processingDate);
		} else {
			metricsService.addCustomParameter("warning-previous-construct-unpublished-update-failed", item.getId());
		}

		// Update record in Construct to DRAFT status.
		constructService.updateParticipationItemStatus(
				item.getId(),
				ParticipationItemStatus.DRAFT,
				null,
				processingDate,
				item.getLastModifiedUserId()
		);
	}
}
