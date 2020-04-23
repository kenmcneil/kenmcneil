package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.newrelic.api.agent.NewRelic;

/**
 * Poll for user events and process them.
 * Poll for time-based events and process them.
 */
public class ParticipationProcessor {
	private final static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);

	private final ParticipationEngineSettings participationEngineSettings;
	private final ConstructService constructService;
	private final ParticipationService participationService;
	private final ParticipationWriter participationWriter;

	public ParticipationProcessor(
			ParticipationEngineSettings participationEngineSettings,
			ConstructService constructService,
			ParticipationService participationService,
			ParticipationWriter participationWriter
	) {
		this.participationEngineSettings = participationEngineSettings;
		this.constructService = constructService;
		this.participationService = participationService;
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

		// Process pending time-based events for activation and deactivation.
		// This is currently implemented as mongodb queries on the participationItem collection.
		processPendingDeactivations();
		processPendingActivations();
	}

	/**
	 * Unpublish each participation that's pending unpublish.
	 */
	public void processPendingUnpublishes() {
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation(participationEngineSettings.getTestModeMinParticipationId());
		while (item != null) {
			try {
				participationWriter.processUnpublish(item, getProcessingDate());
				LOG.info("participation {} unpublished to draft status", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error unpublishing participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = constructService.getNextPendingUnpublishParticipation(participationEngineSettings.getTestModeMinParticipationId());
		}
	}

	/**
	 * Activate each participation that's pending activation.
	 */
	public void processPendingActivations() {
		Date processingDate = getProcessingDate();
		ParticipationItem item = participationService.getNextParticipationPendingActivation(processingDate);
		while (item != null) {
			try {
				participationWriter.processActivation(item, processingDate);
				LOG.info("participation {} activated by scheduling", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error activating participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			processingDate = getProcessingDate();
			item = participationService.getNextParticipationPendingActivation(processingDate);
		}
	}

	/**
	 * Deactivate each participation that's pending deactivation.
	 */
	public void processPendingDeactivations() {
		Date processingDate = getProcessingDate();
		ParticipationItem item = participationService.getNextParticipationPendingDeactivation(processingDate);
		while (item != null) {
			try {
				participationWriter.processDeactivation(item, processingDate);
				LOG.info("participation {} deactivated and archived by scheduling", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error deactivating participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			processingDate = getProcessingDate();
			item = participationService.getNextParticipationPendingDeactivation(processingDate);
		}
	}
}
