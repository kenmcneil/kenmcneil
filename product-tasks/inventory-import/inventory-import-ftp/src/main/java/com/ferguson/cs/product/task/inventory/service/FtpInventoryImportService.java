package com.ferguson.cs.product.task.inventory.service;

import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;

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
