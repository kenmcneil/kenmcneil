package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.metrics.MetricsServiceUtil;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;

/**
 * Poll for user events and process them.
 * Poll for time-based events and process them.
 */
public class ParticipationProcessor {
	private final static Logger LOG = LoggerFactory.getLogger(ParticipationProcessor.class);

	private final ParticipationEngineSettings participationEngineSettings;
	private final ConstructService constructService;
	private final ParticipationLifecycleService participationLifecycleService;
	private final ParticipationWriter participationWriter;

	public ParticipationProcessor(
			ParticipationEngineSettings participationEngineSettings,
			ConstructService constructService,
			ParticipationLifecycleService participationLifecycleService,
			ParticipationWriter participationWriter
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.constructService = constructService;
		this.participationLifecycleService = participationLifecycleService;
		this.participationWriter = participationWriter;
	}

	public Date getProcessingDate() {
		return new Date();
	}

	/**
	 * Poll for new events and process each one.
	 */
	public void process() {
		// Process pending user events.
		// This is currently implemented as mongodb queries on the participationItem collection.
		processPendingUnpublishes();
		processPendingPublishes();

		// Process pending time-based events for activation and deactivation.
		// This is currently implemented as mongodb queries on the participationItem collection.
		processPendingDeactivations();
		processPendingActivations();
	}

	/**
	 * Publish each participation that's pending publish. Polls for a ParticipationItem from Construct to publish,
	 * processes it, and repeats until no more records to process.
	 */
	public void processPendingPublishes() {
		ParticipationItem item = constructService.getNextPendingPublishParticipation(
				participationEngineSettings.getTestModeMinParticipationId());
		while (item != null) {
				participationWriter.processPublish(item, getProcessingDate());
				LOG.info("participation {} published", item.getId());
			item = constructService.getNextPendingPublishParticipation(
					participationEngineSettings.getTestModeMinParticipationId());
		}
	}

	/**
	 * Unpublish each participation that's pending unpublish.
	 */
	public void processPendingUnpublishes() {
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation(
				participationEngineSettings.getTestModeMinParticipationId());
		while (item != null) {
			try {
				participationWriter.processUnpublish(item, getProcessingDate());
				LOG.info("participation {} unpublished to draft status", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error unpublishing participation " + item.getId();
				MetricsServiceUtil.getInstance().noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = constructService.getNextPendingUnpublishParticipation(
					participationEngineSettings.getTestModeMinParticipationId());
		}
	}

	/**
	 * Activate each participation that's pending activation.
	 */
	public void processPendingActivations() {
		Date processingDate = getProcessingDate();
		ParticipationItemPartial itemPartial = participationLifecycleService.getNextParticipationPendingActivation(processingDate);

		while (itemPartial != null) {
			try {
				participationWriter.processActivation(itemPartial, processingDate);
				LOG.info("participation {} activated by scheduling", itemPartial.getParticipationId());
			} catch (Exception e) {
				String errorMessage = "Error activating participation " + itemPartial.getParticipationId();
				MetricsServiceUtil.getInstance().noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			processingDate = getProcessingDate();
			itemPartial = participationLifecycleService.getNextParticipationPendingActivation(processingDate);
		}
	}

	/**
	 * Deactivate each participation that's pending deactivation.
	 */
	public void processPendingDeactivations() {
		Date processingDate = getProcessingDate();
		ParticipationItemPartial itemPartial = participationLifecycleService.getNextExpiredParticipation(processingDate);
		while (itemPartial != null) {
			try {
				participationWriter.processDeactivation(itemPartial, processingDate);
				if (itemPartial.getIsActive()) {
					LOG.info("expired participation {} deactivated and unpublished with archived status", itemPartial.getParticipationId());
				} else {
					LOG.info("never activated expired participation {} unpublished with archived status", itemPartial.getParticipationId());
				}
			} catch (Exception e) {
				String errorMessage = "Error deactivating or unpublishing participation " + itemPartial.getParticipationId();
				MetricsServiceUtil.getInstance().noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			processingDate = getProcessingDate();
			itemPartial = participationLifecycleService.getNextExpiredParticipation(processingDate);
		}
	}
}
