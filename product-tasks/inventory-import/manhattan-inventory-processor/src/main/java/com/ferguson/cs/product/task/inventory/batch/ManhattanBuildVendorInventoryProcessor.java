package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

public class ManhattanBuildVendorInventoryProcessor implements ItemProcessor<VendorInventory, VendorInventory> {

	private ManhattanInventoryJob manhattanInventoryJob;

	@Autowired
	public void setManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob) {
		this.manhattanInventoryJob = manhattanInventoryJob;
	}

	@Override
	public VendorInventory process(VendorInventory vendorInventory) throws Exception {

		if (validate(vendorInventory)) {
			String[] locationParts = vendorInventory.getLocation().split("_");
			String location = locationParts.length > 1 ? locationParts[1] : vendorInventory.getLocation();
			VendorInventory copy = new VendorInventory();
			copy.setMpid(vendorInventory.getMpid());
			copy.setManhattanInventoryJobId(vendorInventory.getManhattanInventoryJobId());
			copy.setQuantity(vendorInventory.getQuantity());
			copy.setLocation(location);
			return copy;
		}
		return null;
	}

	private boolean validate(VendorInventory vendorInventory) {
		return vendorInventory != null &&
				vendorInventory.getMpid() != null &&
				vendorInventory.getLocation() != null;
	}
}
