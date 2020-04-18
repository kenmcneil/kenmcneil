package com.ferguson.cs.product.stream.participation.engine;

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

	private final ConstructService constructService;
	private final ParticipationWriter participationWriter;

	public ParticipationProcessor(ConstructService constructService, ParticipationWriter participationWriter) {
		this.constructService = constructService;
		this.participationWriter = participationWriter;
	}

	/**
	 * Unpublish each participation that's pending unpublish.
	 */
	public void processPendingUnpublishes() {
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation();
		while (item != null) {
			try {
				participationWriter.processUnpublish(item);
				LOG.info("unpublished participation {} to draft status", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error unpublishing participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = constructService.getNextPendingUnpublishParticipation();
		}
	}

	/**
	 * Activate each participation that's pending activation.
	 */
	public void processPendingActivations() {
		ParticipationItem item = constructService.getNextPendingActivationParticipation();
		while (item != null) {
			try {
				participationWriter.processActivation(item);
				LOG.info("activated participation {}", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error activating participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = constructService.getNextPendingActivationParticipation();
		}
	}

	/**
	 * Deactivate each participation that's pending deactivation.
	 */
	public void processPendingDeactivations() {
		ParticipationItem item = constructService.getNextPendingDeactivationParticipation();
		while (item != null) {
			try {
				participationWriter.processDeactivation(item);
				LOG.info("deactivated participation {} to archived status", item.getId());
			} catch (Exception e) {
				String errorMessage = "Error deactivating participation " + item.getId();
				NewRelic.noticeError(errorMessage);
				throw new RuntimeException(errorMessage, e);
			}

			item = constructService.getNextPendingDeactivationParticipation();
		}
	}
}
