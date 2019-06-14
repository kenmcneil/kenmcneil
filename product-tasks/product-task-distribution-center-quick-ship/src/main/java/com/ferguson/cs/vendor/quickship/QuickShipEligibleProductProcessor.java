package com.ferguson.cs.vendor.quickship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.vendor.quickship.model.product.Product;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRule;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideRuleSearchCriteria;
import com.ferguson.cs.vendor.quickship.model.product.ProductLeadTimeOverrideType;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;
import com.ferguson.cs.vendor.quickship.service.product.ProductService;
import com.ferguson.cs.vendor.quickship.service.vendor.VendorService;

public class QuickShipEligibleProductProcessor implements ItemProcessor<List<Product>, List<DistributionCenterProductQuickShip>> {
	private final ProductService productService;
	private final VendorService vendorService;

	public QuickShipEligibleProductProcessor(ProductService productService, VendorService vendorService) {
		this.productService = productService;
		this.vendorService = vendorService;
	}

	@Override
	public List<DistributionCenterProductQuickShip> process(List<Product> productList) {
		List<DistributionCenterProductQuickShip> distributionCenterProductQuickShipList = new ArrayList<>();

		for (Product product : productList) {
			//Ensure that product does not have a lead time override (Made To Order, PreOrder) and that it ships free
			if (!isQuickShipLeadTime(product.getId()) || !productService.productIsFreeShipping(product)) {
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

	/**
	 * Helper method to ensure that the provided product does not have a lead time override rule of types Made To Order
	 * and PreOrder
	 * @param productId
	 * @return
	 */
	private boolean isQuickShipLeadTime(int productId) {
		List<ProductLeadTimeOverrideType> quickShipTypeList = Arrays.asList(ProductLeadTimeOverrideType.MADE_TO_ORDER,
				ProductLeadTimeOverrideType.PRE_ORDER);

		ProductLeadTimeOverrideRuleSearchCriteria leadTimeCriteria = new ProductLeadTimeOverrideRuleSearchCriteria();
		leadTimeCriteria.setProductId(productId);
		leadTimeCriteria.setTypeList(quickShipTypeList);

		List<ProductLeadTimeOverrideRule> ruleList = productService.getLeadTimeOverrideRuleList(leadTimeCriteria);
		if (!ruleList.isEmpty()) {
			return false;
		}

		return true;
	}
}
