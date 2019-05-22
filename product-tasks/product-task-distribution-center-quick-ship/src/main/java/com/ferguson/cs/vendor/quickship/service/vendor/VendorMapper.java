package com.ferguson.cs.vendor.quickship.service.vendor;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;

@Mapper
public interface VendorMapper {
	List<DistributionCenter> getFergusonQuickShipDistributionCenterList(
			@Param("criteria") QuickShipDistributionCenterSearchCriteria criteria);

	void insertDistributionCenterProductQuickShip(DistributionCenterProductQuickShip distributionCenterProductQuickShip);
	DistributionCenterProductQuickShip getDistributionCenterProductQuickShip(int id);
	void truncateVendorProductQuickShipTable();
}
