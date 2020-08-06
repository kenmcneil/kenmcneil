package com.ferguson.cs.product.stream.participation.engine;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ParticipationEngineTask {
	private final static Logger LOG = LoggerFactory.getLogger(ParticipationEngineTask.class);

	private final ParticipationEngineSettings participationEngineSettings;
	private final ParticipationProcessor participationProcessor;

	public ParticipationEngineTask(ParticipationProcessor participationProcessor, ParticipationEngineSettings participationEngineSettings) {
		this.participationProcessor = participationProcessor;
		this.participationEngineSettings = participationEngineSettings;

		if (BooleanUtils.isNotTrue(participationEngineSettings.getProcessingEnabled())) {
			LOG.warn("Processing is disabled.");
		}

		if (BooleanUtils.isTrue(participationEngineSettings.getTestModeEnabled())) {
			LOG.info("Test mode enabled. Only processing participation records with id equal or greater to {}", participationEngineSettings.getTestModeMinParticipationId());
		}
	}

	/**
	 * Periodically poll for new events to handle. Delay at first to allow startup
	 * processes to finish their log output (this makes the log easier to read).
	 */
	@Scheduled(fixedDelayString = "${participation-engine.schedule-fixed-delay:60000}",
			initialDelayString = "${participation-engine.schedule-initial-delay:2000}")
	public void pollForEvents() {
		if (BooleanUtils.isTrue(participationEngineSettings.getProcessingEnabled())) {
			participationProcessor.process();
		}
	}
}
