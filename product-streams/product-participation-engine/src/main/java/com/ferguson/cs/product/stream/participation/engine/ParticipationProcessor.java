package com.ferguson.cs.product.stream.participation.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;

/**
 * Poll for user events and process them.
 * Poll for time-based events and process them.
 */
public class ParticipationProcessor {
	private final static Logger LOG = LoggerFactory.getLogger(ParticipationServiceImpl.class);
	private final ObjectMapper mapper = new ObjectMapper();

	private final ParticipationReader participationReader;
	private final ParticipationWriter participationWriter;

	public ParticipationProcessor(ParticipationReader participationReader, ParticipationWriter participationWriter) {
		this.participationReader = participationReader;
		this.participationWriter = participationWriter;
	}

	/**
	 * Unpublish each participation that's pending unpublish.
	 */
	public void processPendingUnpublishes() {
		int previousParticipationId = 0;

		ParticipationItem item = participationReader.getNextPendingUnpublishParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Tried to process participation " + item.getId() + " again - status update may have failed.");
				break;
			}

			LOG.trace(toJson(item));
			participationWriter.processUnpublish(item);
			LOG.info("unpublished participation " + item.getId() + " to draft status");

			previousParticipationId = item.getId();
			item = participationReader.getNextPendingUnpublishParticipation();
		}
	}

	/**
	 * Activate each participation that's pending activation.
	 */
	public void processPendingActivations() {
		int previousParticipationId = 0;

		ParticipationItem item = participationReader.getNextPendingActivationParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Tried to process participation " + item.getId() + " again - status update may have failed.");
				break;
			}

			LOG.trace(toJson(item));
			participationWriter.processActivation(item);
			LOG.info("activated participation " + item.getId());

			previousParticipationId = item.getId();
			item = participationReader.getNextPendingActivationParticipation();
		}
	}

	/**
	 * Deactivate each participation that's pending deactivation.
	 */
	public void processPendingDeactivations() {
		int previousParticipationId = 0;

		ParticipationItem item = participationReader.getNextPendingDeactivationParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Tried to process participation " + item.getId() + " again - status update may have failed.");
				break;
			}

			LOG.trace(toJson(item));
			participationWriter.processDeactivation(item);
			LOG.info("deactivated participation " + item.getId() + " to archived status");

			previousParticipationId = item.getId();
			item = participationReader.getNextPendingDeactivationParticipation();
		}
	}

	/**
	 * Convert given object to a JSON string for debug output.
	 */
	private String toJson(Object item) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item);
		} catch (Exception e) {
			LOG.debug("Could not convert to JSON: ", e);
		}
		return "";
	}
}
