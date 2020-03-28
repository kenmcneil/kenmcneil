package com.ferguson.cs.product.task.inventory.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryResponse;

@FeignClient(
		name = "electrolux-api-client",
		url = "${electrolux.api-url}",
		configuration = {ElectroluxClientConfiguration.class})
@Component
public interface ElectroluxFeignClient {
	@GetMapping("/inventoryv1")
	ElectroluxInventoryResponse getElectroluxInventoryData(@RequestParam("warehouseCode") String warehouseCode,@RequestParam("skus")String skus);

}
