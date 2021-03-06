package com.ferguson.cs.product.task.inventory.dao.reporter;

import java.util.List;

import com.ferguson.cs.product.task.inventory.model.VendorFtpData;

public interface FtpInventoryDao {
	/**
	 * Gets data for all vendors that have ftp enabled
	 *
	 * @return list of vendor ftp data for all relevant vendors
	 */
	List<VendorFtpData> getVendorFtpData();
}
