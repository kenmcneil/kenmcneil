package com.ferguson.cs.product.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.service.inventory.FtpInventoryImportService;

@Service
public class FtpInventoryImportTaskImpl implements FtpInventoryImportTask {

	private FtpInventoryImportService ftpInventoryImportService;

	@Autowired
	public void setFtpInventoryImportService(FtpInventoryImportService ftpInventoryImportService) {
		this.ftpInventoryImportService = ftpInventoryImportService;
	}

	@Override
	public void importInventoryViaFtp() {
		ftpInventoryImportService.downloadVendorInventoryFiles();
	}
}
