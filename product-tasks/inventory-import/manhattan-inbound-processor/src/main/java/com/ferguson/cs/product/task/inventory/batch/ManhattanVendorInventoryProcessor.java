package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.LocationAvailabilityResponse;

public class ManhattanVendorInventoryProcessor implements ItemProcessor<LocationAvailabilityResponse,VendorInventory> {
	@Override
	public VendorInventory process(LocationAvailabilityResponse locationAvailabilityResponse) throws Exception {
		if(validate(locationAvailabilityResponse)) {
			VendorInventory vendorInventory = new VendorInventory();
			vendorInventory.setMpn(locationAvailabilityResponse.getItemId());
			vendorInventory.setLocation(locationAvailabilityResponse.getLocationId());
			vendorInventory.setQuantity(locationAvailabilityResponse.getQuantity());
			vendorInventory.setStatus(locationAvailabilityResponse.getStatus());
			vendorInventory.setEta(locationAvailabilityResponse.getNextAvailabilityDate());
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
