package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
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
		ParticipationItemPartial item = getNextPendingUnpublishParticipation();
		while (item != null) {
			try {
				participationWriter.processUnpublish(item, getProcessingDate());
				LOG.info("participation {} unpublished to draft status", item.getParticipationId());
			} catch (Exception e) {
				String errorMessage = "Error unpublishing participation " + item.getParticipationId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = getNextPendingUnpublishParticipation();
		}
	}

	/**
	 * Activate each participation that's pending activation.
	 */
	public void processPendingActivations() {
		Date processingDate = getProcessingDate();
		ParticipationItemPartial item = participationService.getNextParticipationPendingActivation(processingDate);
		while (item != null) {
			try {
				participationWriter.processActivation(item, processingDate);
				LOG.info("participation {} activated by scheduling", item.getParticipationId());
			} catch (Exception e) {
				String errorMessage = "Error activating participation " + item.getParticipationId();
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
		ParticipationItemPartial item = participationService.getNextExpiredParticipation(processingDate);
		while (item != null) {
			try {
				participationWriter.processDeactivation(item, processingDate);
				if (item.getIsActive()) {
					LOG.info("expired participation {} deactivated and unpublished with archived status", item.getParticipationId());
				} else {
					LOG.info("never activated expired participation {} unpublished with archived status", item.getParticipationId());
				}
			} catch (Exception e) {
				String errorMessage = "Error deactivating or unpublishing participation " + item.getParticipationId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			processingDate = getProcessingDate();
			item = participationService.getNextExpiredParticipation(processingDate);
		}
	}

	/**
	 * Get the next pending unpublish event from Construct as a ParticipationItemPartial.
	 * Populates only the participationId and lastModifiedUserId since that's what comes from the Construct service.
	 */
	private ParticipationItemPartial getNextPendingUnpublishParticipation() {
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation(participationEngineSettings
				.getTestModeMinParticipationId());
		return item == null ? null : ParticipationItemPartial.builder()
				.participationId(item.getId())
				.lastModifiedUserId(item.getLastModifiedUserId())
				.build();
	}
}
