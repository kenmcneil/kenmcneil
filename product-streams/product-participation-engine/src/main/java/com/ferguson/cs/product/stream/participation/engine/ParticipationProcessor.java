package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.lifecycle.ParticipationLifecycleService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItemPartial;
import com.newrelic.api.agent.NewRelic;

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
	 * Publish each participation that's pending publish.
	 */
	public void processPendingPublishes() {
		ParticipationItem item = constructService.getNextPendingPublishParticipation(
				participationEngineSettings.getTestModeMinParticipationId());
		while (item != null) {
			try {
				participationWriter.processPublish(item, getProcessingDate());
				LOG.info("participation {} published", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error publishing participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = constructService.getNextPendingPublishParticipation(
					participationEngineSettings.getTestModeMinParticipationId());
		}
	}

	/**
	 * Unpublish each participation that's pending unpublish.
	 */
	public void processPendingUnpublishes() {
		ParticipationItemPartial itemPartial = getNextPendingUnpublishParticipation();
		while (itemPartial != null) {
			try {
				participationWriter.processUnpublish(itemPartial, getProcessingDate());
				LOG.info("participation {} unpublished to draft status", itemPartial.getParticipationId());
			} catch (Exception e) {
				String errorMessage = "Error unpublishing participation " + itemPartial.getParticipationId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			itemPartial = getNextPendingUnpublishParticipation();
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
				NewRelic.noticeError(errorMessage);
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
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			processingDate = getProcessingDate();
			itemPartial = participationLifecycleService.getNextExpiredParticipation(processingDate);
		}
	}

	/**
	 * Get the next pending unpublish event from Construct as a ParticipationItemPartial.
	 * Populates only the participationId and lastModifiedUserId since that's what comes from the
	 * Construct service and is all that's needed to process the unpublish.
	 */
	private ParticipationItemPartial getNextPendingUnpublishParticipation() {
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation(
				participationEngineSettings.getTestModeMinParticipationId());
		return item == null ? null : ParticipationItemPartial.builder()
				.participationId(item.getId())
				.lastModifiedUserId(item.getLastModifiedUserId())
				.build();
	}
}
