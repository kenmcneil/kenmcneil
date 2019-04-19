package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;

public class ManhattanVendorInventoryProcessor implements ItemProcessor<VendorInventory,VendorInventory> {

	private ManhattanIntakeJob manhattanIntakeJob;

	@Autowired
	public void setManhattanIntakeJob(ManhattanIntakeJob manhattanIntakeJob) {
		this.manhattanIntakeJob = manhattanIntakeJob;
	}

	@Override
	public VendorInventory process(VendorInventory vendorInventory) throws Exception {
		if(validate(vendorInventory)) {
			vendorInventory.setTransactionNumber(manhattanIntakeJob.getTransactionNumber());
			return vendorInventory;
		}
		return null;
	}

	private boolean validate(VendorInventory vendorInventory) {
		return vendorInventory != null &&
				vendorInventory.getSku() != null &&
				vendorInventory.getLocation() != null;
	}
}
