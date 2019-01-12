package com.ferguson.cs.product.service.inventory;

import com.ferguson.cs.product.model.FtpInventoryImportJobLog;

public interface FtpInventoryImportService {
	/**
	 * Downloads vendor inventory files for all vendors with FTP
	 */
	void downloadVendorInventoryFiles();

	/**
	 * Saves log data for ftp inventory import job
	 *
	 * @param ftpInventoryImportJobLog
	 * @return Updated/Inserted FtpInventoryImportJobLog
	 */
	FtpInventoryImportJobLog saveFtpInventoryImportJobLog(FtpInventoryImportJobLog ftpInventoryImportJobLog);
}
