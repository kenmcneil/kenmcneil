package com.ferguson.cs.product.task.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.dao.InventoryImportJobDao;
import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobLog;

@Service
public class InventoryImportJobLogServiceImpl implements InventoryImportJobLogService {
	private InventoryImportJobDao inventoryImportJobDao;

	@Autowired
	public void setInventoryImportJobDao(InventoryImportJobDao inventoryImportJobDao) {
		this.inventoryImportJobDao = inventoryImportJobDao;
	}

	@Override
	public void saveInventoryImportJobLog(InventoryImportJobLog inventoryImportJobLog) {
		if(inventoryImportJobLog instanceof FtpInventoryImportJobLog) {
			FtpInventoryImportJobLog ftpInventoryImportJobLog  = (FtpInventoryImportJobLog)inventoryImportJobLog;
			if(ftpInventoryImportJobLog.getId() == null) {
				inventoryImportJobDao.insertFtpInventoryImportJobLog(ftpInventoryImportJobLog);
			} else {
				inventoryImportJobDao.updateFtpInventoryImportJobLog(ftpInventoryImportJobLog);
			}
		} else if(inventoryImportJobLog instanceof EmailInventoryImportJobLog) {
			EmailInventoryImportJobLog emailInventoryImportJobLog = (EmailInventoryImportJobLog)inventoryImportJobLog;
			if(emailInventoryImportJobLog.getId() == null) {
				inventoryImportJobDao.insertEmailInventoryImportJobLog(emailInventoryImportJobLog);
			} else {
				inventoryImportJobDao.updateEmailInventoryImportJobLog(emailInventoryImportJobLog);
			}
		}
	}
}
