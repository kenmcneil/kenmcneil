package com.ferguson.cs.vendor.quickship.service.shipping;

import org.springframework.stereotype.Repository;
import com.ferguson.cs.vendor.quickship.model.shipping.ShippingCalculationView;

@Repository
public class ShippingDaoImpl implements ShippingDao {

	private final ShippingMapper shippingMapper;

	public ShippingDaoImpl(ShippingMapper shippingMapper) {
		this.shippingMapper = shippingMapper;
	}


	@Override
	public ShippingCalculationView getStoreShippingCalculationView(Integer siteId, Integer storeId, Integer shippingCalculationNameId) {
		return shippingMapper
				.getStoreShippingCalculationView(siteId, storeId, shippingCalculationNameId);
	}

	@Override
	public ShippingCalculationView getUniqueIdShippingCalculationView(Integer genericCategoryRootId, Integer productUniqueId, Integer shippingCalculationNameId) {
		return shippingMapper
				.getUniqueIdShippingCalculationView(genericCategoryRootId, productUniqueId, shippingCalculationNameId);
	}
}
