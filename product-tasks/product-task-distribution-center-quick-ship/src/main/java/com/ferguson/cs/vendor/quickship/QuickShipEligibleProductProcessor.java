package com.ferguson.cs.vendor.quickship;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.vendor.quickship.model.category.ShippingCalculationView;
import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;
import com.ferguson.cs.vendor.quickship.service.category.CategoryService;
import com.ferguson.cs.vendor.quickship.service.product.ProductService;
import com.ferguson.cs.vendor.quickship.service.vendor.VendorService;

public class QuickShipEligibleProductProcessor implements ItemProcessor<List<Product>, List<DistributionCenterProductQuickShip>> {
	private final ProductService productService;
	private final VendorService vendorService;
	private ShippingCalculationView buildShippingCalculationView;
	private static final int BUILD_SITE_ID = 82;
	private static final int BUILD_STORE_ID = 248;
	private static final int STANDARD_DELIVERY_CALCULATION_NAME_ID = 68;

	public QuickShipEligibleProductProcessor(ProductService productService, VendorService vendorService, CategoryService categoryService) {
		this.productService = productService;
		this.vendorService = vendorService;
		buildShippingCalculationView = categoryService.getStoreShippingCalculationView(BUILD_SITE_ID,BUILD_STORE_ID,STANDARD_DELIVERY_CALCULATION_NAME_ID);
	}

	@Override
	public List<DistributionCenterProductQuickShip> process(List<Product> productList) {
		List<DistributionCenterProductQuickShip> distributionCenterProductQuickShipList = new ArrayList<>();

		for (Product product : productList) {
			//Ensure that product ships free
			if (!productService.isFreeShipping(product,buildShippingCalculationView)) {
				continue;
			}

			QuickShipDistributionCenterSearchCriteria criteria = new QuickShipDistributionCenterSearchCriteria();
			criteria.setProductId(product.getFamily().getProductId());
			criteria.setFinishDescription(product.getFinish().getDescription());
			criteria.setManufacturerName(product.getFamily().getManufacturer().getName());

			List<DistributionCenter> distributionCenterList =
					vendorService.getFergusonQuickShipDistributionCenterList(criteria);
			if (distributionCenterList.isEmpty()) {
				continue;
			}

			for (DistributionCenter distributionCenter : distributionCenterList) {
				DistributionCenterProductQuickShip distributionCenterProductQuickShip =
						new DistributionCenterProductQuickShip();
				distributionCenterProductQuickShip.setDistributionCenter(distributionCenter);
				distributionCenterProductQuickShip.setProduct(product);

				distributionCenterProductQuickShipList.add(distributionCenterProductQuickShip);
			}
		}

		return distributionCenterProductQuickShipList;
	}
}
