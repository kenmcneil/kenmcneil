package com.ferguson.cs.product.task.inventory.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferguson.cs.product.task.inventory.ElectroluxInventorySettings;

@Configuration
public class ElectroluxClientConfiguration {

	private final ElectroluxInventorySettings electroluxInventorySettings;

	public ElectroluxClientConfiguration(ElectroluxInventorySettings electroluxInventorySettings) {
		this.electroluxInventorySettings = electroluxInventorySettings;
	}

	@Bean
	public WebClient electroluxWebClient(WebClient.Builder webClientBuilder) {
		return webClientBuilder.baseUrl(electroluxInventorySettings.getApiUrl()).defaultHeader("X-IBM-Client-Id",electroluxInventorySettings.getClientId()).build();
	}
}
