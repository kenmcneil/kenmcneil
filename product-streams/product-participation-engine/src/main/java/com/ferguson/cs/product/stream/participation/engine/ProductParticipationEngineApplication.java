package com.ferguson.cs.product.stream.participation.engine;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.model.ParticipationItem;
import com.ferguson.cs.product.stream.participation.engine.sql.ParticipationService;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class ProductParticipationEngineApplication {

	private static Logger LOG = LoggerFactory.getLogger(ProductParticipationEngineApplication.class);

	@Autowired
	private ConstructService constructService;

	@Autowired
	private ParticipationService participationService;

	public static void main(String[] args) {
		SpringApplication.run(ProductParticipationEngineApplication.class, args);
	}

	private boolean printParticipationToLog(ParticipationItem item) {
		if (item.getId() >= 5000) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				LOG.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(item));
				return true;
			} catch (Exception e) {
				// ignore
			}
		}
		return false;
	}

	@Scheduled(fixedDelay = 1000)
	public void eventPoller() {
		processPendingActivations();
		processPendingDeactivations();
		processPendingUnpublishes();


		System.out.println("test");
	}

	private void processPendingActivations() {
		int previousParticipationId = 0;

		// activate each pending participation
		ParticipationItem item = constructService.getNextPendingActivationParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Got same participation as last time, status update failed???");
				break;
			}
			previousParticipationId = item.getId();
			printParticipationToLog(item);
			//participationService.activateParticipation(item, new Date());
			item = constructService.getNextPendingActivationParticipation();
		}
	}

	private void processPendingDeactivations() {
		int previousParticipationId = 0;

		// activate each pending participation
		ParticipationItem item = constructService.getNextPendingDeactivationParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Got same participation as last time, status update failed???");
				break;
			}
			previousParticipationId = item.getId();
			printParticipationToLog(item);
//			participationService.deactivateParticipation(item, new Date());
			item = constructService.getNextPendingDeactivationParticipation();
		}
	}

	private void processPendingUnpublishes() {
		int previousParticipationId = 0;

		// activate each pending participation
		ParticipationItem item = constructService.getNextPendingUnpublishParticipation();
		while (item != null) {
			if (item.getId() == previousParticipationId) {
				LOG.error("Got same participation as last time, status update failed???");
				break;
			}
			previousParticipationId = item.getId();
			printParticipationToLog(item);
			//participationService.activateParticipation(item, new Date());
			item = constructService.getNextPendingUnpublishParticipation();
		}
	}


}
