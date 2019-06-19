package com.ferguson.cs.vendor.quickship.service.shipping;

import org.springframework.stereotype.Service;
import com.ferguson.cs.vendor.quickship.model.shipping.ShippingCalculationView;

@Service
public class ShippingServiceImpl implements ShippingService {

	private final ShippingDao shippingDao;


	ShippingServiceImpl(ShippingDao shippingDao) {
		this.shippingDao = shippingDao;
	}

	@Override
	public ShippingCalculationView getStoreShippingCalculationView(Integer siteId, Integer storeId, Integer shippingCalculationNameId) {
		return shippingDao.getStoreShippingCalculationView(siteId, storeId, shippingCalculationNameId);
	}

	@Override
	public ShippingCalculationView getUniqueIdShippingCalculationView(Integer genericCategoryRootId, Integer productUniqueId, Integer shippingCalculationNameId) {
		return shippingDao
				.getUniqueIdShippingCalculationView(genericCategoryRootId, productUniqueId, shippingCalculationNameId);
	}
}
