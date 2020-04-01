package com.ferguson.cs.product.stream.participation.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class ParticipationEngineApplication {

	private static Logger LOG = LoggerFactory.getLogger(ParticipationEngineApplication.class);

	private ParticipationService participationService;

	@Autowired
	public void setParticipationService(ParticipationService participationService) {
		this.participationService = participationService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ParticipationEngineApplication.class, args);
	}

	/**
	 * Periodically poll for new events to handle. Delay at first to allow startup
	 * processes to finish their log output (this makes the log easier to read).
	 */
	@Scheduled(fixedDelayString = "${settings.schedule-fixed-delay:1000}",
			initialDelayString = "${settings.schedule-initial-delay:2000}")
	public void pollForEvents() {
		// Process pending user events.
		participationService.processPendingUnpublishes();

		// Process pending time-based events for activation and deactivation.
		participationService.processPendingDeactivations();
		participationService.processPendingActivations();
	}

	// TODO implement shutdown hook to ensure that processing is completed on the current participation (if any).
}
