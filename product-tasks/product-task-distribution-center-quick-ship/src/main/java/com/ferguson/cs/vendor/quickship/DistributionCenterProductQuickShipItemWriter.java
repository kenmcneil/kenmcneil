package com.ferguson.cs.vendor.quickship;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.service.vendor.VendorService;

public class DistributionCenterProductQuickShipItemWriter implements ItemWriter<List<DistributionCenterProductQuickShip>> {
	private final VendorService vendorService;

	public DistributionCenterProductQuickShipItemWriter(VendorService vendorService) {
		this.vendorService = vendorService;
	}

	@Override
	public void write(List<? extends List<DistributionCenterProductQuickShip>> distributionCenterProductQuickShipListSet) {
		if (distributionCenterProductQuickShipListSet == null || distributionCenterProductQuickShipListSet.isEmpty()) {
			return;
		}

		for (List<DistributionCenterProductQuickShip> distributionCenterProductQuickShipList : distributionCenterProductQuickShipListSet) {
			for (DistributionCenterProductQuickShip distributionCenterProductQuickShip : distributionCenterProductQuickShipList) {
				vendorService.insertDistributionCenterProductQuickShip(distributionCenterProductQuickShip);
			}
		}
	}
}
