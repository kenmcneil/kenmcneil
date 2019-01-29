package com.ferguson.cs.product.task.inventory;


import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.service.InventoryImportService;


@Service
public class EmailInventoryImportTaskImpl implements EmailInventoryImportTask {

	private InventoryImportService emailInventoryImportService;

	@Autowired
	@Qualifier("emailInventoryImportService")
	public void setEmailInventoryImportService(InventoryImportService emailInventoryImportService) {
		this.emailInventoryImportService = emailInventoryImportService;
	}


	@Override
	public void importInventoryViaEmail() throws IOException, MessagingException {
		emailInventoryImportService.importInventory();
	}
}
