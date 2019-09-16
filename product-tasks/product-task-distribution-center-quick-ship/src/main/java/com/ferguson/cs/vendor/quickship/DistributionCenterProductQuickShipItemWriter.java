package com.ferguson.cs.vendor.quickship;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.service.product.ProductService;
import com.ferguson.cs.vendor.quickship.service.vendor.VendorService;

public class DistributionCenterProductQuickShipItemWriter implements ItemWriter<List<DistributionCenterProductQuickShip>> {
	private final VendorService vendorService;
	private final ProductService productService;

	public DistributionCenterProductQuickShipItemWriter(VendorService vendorService, ProductService productService) {
		this.vendorService = vendorService;
		this.productService = productService;
	}

	@Override
	public void write(List<? extends List<DistributionCenterProductQuickShip>> distributionCenterProductQuickShipListSet) {
		if (distributionCenterProductQuickShipListSet == null || distributionCenterProductQuickShipListSet.isEmpty()) {
			return;
		}

		for (List<DistributionCenterProductQuickShip> distributionCenterProductQuickShipList : distributionCenterProductQuickShipListSet) {
			for (DistributionCenterProductQuickShip distributionCenterProductQuickShip : distributionCenterProductQuickShipList) {
				vendorService.insertDistributionCenterProductQuickShip(distributionCenterProductQuickShip);
				productService.updateProductModified(distributionCenterProductQuickShip.getProduct());
			}
		}
	}
}
