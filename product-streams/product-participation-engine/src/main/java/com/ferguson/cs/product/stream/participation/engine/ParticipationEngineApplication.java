package com.ferguson.cs.product.stream.participation.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
public class ParticipationEngineApplication {
	public static void main(String[] args) {
		SpringApplication.run(ParticipationEngineApplication.class, args);
	}
}
