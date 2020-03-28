package com.ferguson.cs.product.task.inventory.client;

import org.springframework.context.annotation.Bean;

import com.ferguson.cs.product.task.inventory.ElectroluxInventorySettings;

import feign.auth.BasicAuthRequestInterceptor;

public class ElectroluxClientConfiguration {

	private final ElectroluxInventorySettings electroluxInventorySettings;

	public ElectroluxClientConfiguration(ElectroluxInventorySettings electroluxInventorySettings) {
		this.electroluxInventorySettings = electroluxInventorySettings;
	}

	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor(electroluxInventorySettings.getApiUsername(), electroluxInventorySettings.getApiPassword());
	}
}
