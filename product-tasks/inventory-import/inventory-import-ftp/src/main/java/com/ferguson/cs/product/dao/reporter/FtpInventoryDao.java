package com.ferguson.cs.product.dao.reporter;

import java.util.List;

import com.ferguson.cs.product.model.VendorFtpData;

public interface FtpInventoryDao {
	/**
	 * Gets data for all vendors that have ftp enabled
	 *
	 * @return list of vendor ftp data for all relevant vendors
	 */
	List<VendorFtpData> getVendorFtpData();
}
