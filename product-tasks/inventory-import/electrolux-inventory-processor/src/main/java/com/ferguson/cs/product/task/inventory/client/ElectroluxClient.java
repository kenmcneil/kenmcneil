package com.ferguson.cs.product.task.inventory.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ferguson.cs.product.task.inventory.model.ElectroluxInventoryResponse;

@Component
public interface ElectroluxClient {
	ElectroluxInventoryResponse getElectroluxInventoryData(String warehouseCode, List<String> skus);

}
