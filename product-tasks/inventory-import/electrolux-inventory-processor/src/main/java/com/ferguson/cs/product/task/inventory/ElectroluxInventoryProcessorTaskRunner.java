package com.ferguson.cs.product.task.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ferguson.cs.task.SimpleTaskRunner;

@Component
public class ElectroluxInventoryProcessorTaskRunner implements SimpleTaskRunner {
	private ElectroluxInventoryProcessorTask electroluxInventoryProcessorTask;

	@Autowired
	public void setOrderlyCreateRiskPoTask(ElectroluxInventoryProcessorTask electroluxInventoryProcessorTask) {
		this.electroluxInventoryProcessorTask = electroluxInventoryProcessorTask;
	}

	@Override
	public void runTask() throws Exception {
		electroluxInventoryProcessorTask.writeElectroluxInventoryData();
	}
}
