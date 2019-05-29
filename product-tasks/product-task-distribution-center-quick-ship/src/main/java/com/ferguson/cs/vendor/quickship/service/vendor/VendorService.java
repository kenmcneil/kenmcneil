package com.ferguson.cs.vendor.quickship.service.vendor;

import java.util.List;

import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenter;
import com.ferguson.cs.vendor.quickship.model.vendor.DistributionCenterProductQuickShip;
import com.ferguson.cs.vendor.quickship.model.vendor.QuickShipDistributionCenterSearchCriteria;

public interface VendorService {

	/**
	 * Get the list of Ferguson Quick Ship distribution centers
	 * @param criteria
	 * @return
	 */
	List<DistributionCenter> getFergusonQuickShipDistributionCenterList(
			QuickShipDistributionCenterSearchCriteria criteria);

	/**
	 * Insert distribution center product Quick Ship mapping
	 * @param distributionCenterProductQuickShip
	 */
	void insertDistributionCenterProductQuickShip(
			DistributionCenterProductQuickShip distributionCenterProductQuickShip);

	/**
	 * Get distribution center product Quick Ship mapping
	 * @param id
	 * @return
	 */
	DistributionCenterProductQuickShip getDistributionCenterProductQuickShip(int id);

	/**
	 * Delete all distribution center product Quick Ship mapping records
	 */
	void truncateVendorProductQuickShipTable();
}
