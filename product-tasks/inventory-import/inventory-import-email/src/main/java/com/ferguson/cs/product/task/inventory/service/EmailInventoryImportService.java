package com.ferguson.cs.product.task.inventory.service;

import java.io.IOException;

import javax.mail.MessagingException;

public interface EmailInventoryImportService {
	void importInventoryViaEmail() throws MessagingException, IOException;
}
