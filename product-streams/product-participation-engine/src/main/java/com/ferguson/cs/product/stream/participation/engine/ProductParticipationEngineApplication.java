package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class ProductParticipationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductParticipationEngineApplication.class, args);
	}

	@Scheduled(fixedDelay = 1000)
	public void eventPoller() {
		System.out.println("test");
	}
}
