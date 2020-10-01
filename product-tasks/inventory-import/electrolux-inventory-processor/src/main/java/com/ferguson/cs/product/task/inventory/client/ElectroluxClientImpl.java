package com.ferguson.cs.product.task.inventory.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ferguson.cs.product.task.inventory.ElectroluxInventorySettings;
import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryResponse;

@Component
public class ElectroluxClientImpl implements ElectroluxClient {
	private final WebClient webClient;
	private final ElectroluxInventorySettings electroluxInventorySettings;

	public ElectroluxClientImpl(WebClient webClient, ElectroluxInventorySettings electroluxInventorySettings) {
		this.webClient = webClient;
		this.electroluxInventorySettings = electroluxInventorySettings;
	}


	@Override
	public ElectroluxInventoryResponse getElectroluxInventoryData(String warehouseCode, List<String> skus) {
		return webClient.get().uri(uriBuilder -> uriBuilder.path("").queryParam("warehouseCodes", warehouseCode)
				.queryParam("skus", String.join(",", skus))
				.queryParam("soldTo", electroluxInventorySettings.getCustomerId()).build()).retrieve()
				.bodyToMono(ElectroluxInventoryResponse.class).block();
	}
}
