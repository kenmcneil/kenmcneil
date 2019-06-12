package com.ferguson.cs.vendor.quickship.service.vendor;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;

@Repository
public class VendorDaoImpl implements VendorDao {
	private final VendorMapper vendorMapper;

	public VendorDaoImpl(VendorMapper vendorMapper) {
		this.vendorMapper = vendorMapper;
	}

	@Override
	public List<DistributionCenter> getFergusonQuickShipDistributionCenterList(
			QuickShipDistributionCenterSearchCriteria criteria) {
		return vendorMapper.getFergusonQuickShipDistributionCenterList(criteria);
	}

	@Override
	public void insertDistributionCenterProductQuickShip(
			DistributionCenterProductQuickShip distributionCenterProductQuickShip) {
		vendorMapper.insertDistributionCenterProductQuickShip(distributionCenterProductQuickShip);
	}

	@Override
	public DistributionCenterProductQuickShip getDistributionCenterProductQuickShip(int id) {
		return vendorMapper.getDistributionCenterProductQuickShip(id);
	}

	@Override
	public void truncateVendorProductQuickShipTable() {
		vendorMapper.truncateVendorProductQuickShipTable();
	}

}
