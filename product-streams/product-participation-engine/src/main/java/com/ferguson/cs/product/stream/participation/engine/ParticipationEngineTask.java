package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.scheduling.annotation.Scheduled;

public class ParticipationEngineTask {
	private final ParticipationProcessor participationProcessor;

	public ParticipationEngineTask(ParticipationProcessor participationProcessor) {
		this.participationProcessor = participationProcessor;
	}

	/**
	 * Periodically poll for new events to handle. Delay at first to allow startup
	 * processes to finish their log output (this makes the log easier to read).
	 */
	@Scheduled(fixedDelayString = "${participation-engine.schedule-fixed-delay:1000}",
			initialDelayString = "${participation-engine.schedule-initial-delay:2000}")
	public void pollForEvents() {
		// Process pending user events.
		participationProcessor.processPendingUnpublishes();

		// Process pending time-based events for activation and deactivation.
		participationProcessor.processPendingDeactivations();
		participationProcessor.processPendingActivations();
	}
}
