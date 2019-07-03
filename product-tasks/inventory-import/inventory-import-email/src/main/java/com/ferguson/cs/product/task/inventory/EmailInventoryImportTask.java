package com.ferguson.cs.product.task.inventory;

import java.io.IOException;
import javax.mail.MessagingException;

public interface EmailInventoryImportTask {
	/**
	 * Imports inventory files via FTP and SFTP
	 */
	void importInventoryViaEmail() throws IOException, MessagingException;
}
