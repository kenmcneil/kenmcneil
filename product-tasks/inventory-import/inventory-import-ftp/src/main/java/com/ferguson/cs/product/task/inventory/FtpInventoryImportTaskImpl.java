package com.ferguson.cs.product.task.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.service.InventoryImportService;

@Service
public class FtpInventoryImportTaskImpl implements FtpInventoryImportTask {

	private InventoryImportService ftpInventoryImportService;

	@Autowired
	@Qualifier("ftpInventoryImportService")
	public void setFtpInventoryImportService(InventoryImportService ftpInventoryImportService) {
		this.ftpInventoryImportService = ftpInventoryImportService;
	}

	@Override
	public void importInventoryViaFtp() {
		ftpInventoryImportService.importInventory();
	}
}
