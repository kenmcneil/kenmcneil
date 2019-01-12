package com.ferguson.cs.product.dao.pdm;


import com.ferguson.cs.product.model.FtpInventoryImportJobLog;

public interface InventoryImportJobDao {
	/**
	 * Inserts log data for an ftp inventory import job
	 *
	 * @param ftpInventoryImportJobLog
	 */
	void insertFtpInventoryImportJobLog(FtpInventoryImportJobLog ftpInventoryImportJobLog);

	/**
	 * Updates log data for an ftp inventory import job. Status is updateable, last modified date will be updated to
	 * current time, errors can be inserted. All other fields will remain the same.
	 *
	 * @param ftpInventoryImportJobLog
	 */
	void updateFtpInventoryImportJobLog(FtpInventoryImportJobLog ftpInventoryImportJobLog);
}
