package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.inventory.ManhattanInboundSettings;
import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.LocationAvailabilityResponse;

public class ManhattanVendorInventoryProcessor implements ItemProcessor<LocationAvailabilityResponse,VendorInventory> {

	private ManhattanInboundSettings manhattanInboundSettings;
	private String jobKey;

	@Autowired
	public void setManhattanInboundSettings(ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobKey = stepExecution.getJobExecution().getExecutionContext().getString("jobKey");
	}

	@Override
	public VendorInventory process(LocationAvailabilityResponse locationAvailabilityResponse) throws Exception {
		if(validate(locationAvailabilityResponse)) {
			VendorInventory vendorInventory = new VendorInventory();
			vendorInventory.setMpn(locationAvailabilityResponse.getItemId());
			vendorInventory.setLocation(manhattanInboundSettings.getLocationIdDcMap().get(locationAvailabilityResponse.getLocationId()));
			vendorInventory.setQuantity(locationAvailabilityResponse.getQuantity());
			vendorInventory.setJobKey(jobKey);
			return vendorInventory;
		}
		return null;
	}

	private boolean validate(LocationAvailabilityResponse locationAvailabilityResponse) {
		return locationAvailabilityResponse != null &&
				locationAvailabilityResponse.getItemId() != null &&
				locationAvailabilityResponse.getLocationId() != null;
	}
}
