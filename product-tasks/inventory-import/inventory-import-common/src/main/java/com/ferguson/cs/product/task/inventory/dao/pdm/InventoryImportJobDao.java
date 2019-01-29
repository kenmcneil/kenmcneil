package com.ferguson.cs.product.task.inventory.dao.pdm;


import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;


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

	/**
	 * Inserts log data for an email inventory import job
	 *
	 * @param emailInventoryImportJobLog
	 */
	void insertEmailInventoryImportJobLog(EmailInventoryImportJobLog emailInventoryImportJobLog);

	/**
	 * Updates log data for an email inventory import job. Status is updateable, last modified date will be updated to
	 * current time, errors can be inserted, attachment logs can be inserted. All other fields will remain the same.
	 *
	 * @param emailInventoryImportJobLog
	 */
	void updateEmailInventoryImportJobLog(EmailInventoryImportJobLog emailInventoryImportJobLog);
}
