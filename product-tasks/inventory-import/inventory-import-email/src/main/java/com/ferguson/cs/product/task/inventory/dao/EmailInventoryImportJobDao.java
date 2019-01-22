package com.ferguson.cs.product.task.inventory.dao;

import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;

public interface EmailInventoryImportJobDao {
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
