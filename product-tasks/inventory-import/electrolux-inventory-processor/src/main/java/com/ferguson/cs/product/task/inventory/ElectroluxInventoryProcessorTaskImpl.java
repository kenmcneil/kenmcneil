package com.ferguson.cs.product.task.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.service.ElectroluxInventoryService;

@Service
public class ElectroluxInventoryProcessorTaskImpl implements ElectroluxInventoryProcessorTask {

	private ElectroluxInventoryService electroluxInventoryService;


	@Autowired
	public void setElectroluxInventoryService(ElectroluxInventoryService electroluxInventoryService) {
		this.electroluxInventoryService = electroluxInventoryService;
	}

	@Override
	public void writeElectroluxInventoryData() {
		electroluxInventoryService.writeElectroluxInventoryData();
	}
}
