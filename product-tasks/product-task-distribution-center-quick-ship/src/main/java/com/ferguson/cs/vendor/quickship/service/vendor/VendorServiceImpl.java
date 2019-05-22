package com.ferguson.cs.vendor.quickship.service.vendor;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;

@Service
public class VendorServiceImpl implements VendorService {
	private final VendorDao vendorDao;

	public VendorServiceImpl(VendorDao vendorDao) {
		this.vendorDao = vendorDao;
	}

	@Override
	public List<DistributionCenter> getFergusonQuickShipDistributionCenterList(
			QuickShipDistributionCenterSearchCriteria criteria) {
		return vendorDao.getFergusonQuickShipDistributionCenterList(criteria);
	}

	@Override
	public void insertDistributionCenterProductQuickShip(
			DistributionCenterProductQuickShip distributionCenterProductQuickShip) {
		vendorDao.insertDistributionCenterProductQuickShip(distributionCenterProductQuickShip);
	}

	@Override
	public DistributionCenterProductQuickShip getDistributionCenterProductQuickShip(int id) {
		return vendorDao.getDistributionCenterProductQuickShip(id);
	}

	@Override
	public void truncateVendorProductQuickShipTable() {
		vendorDao.truncateVendorProductQuickShipTable();
	}
}
