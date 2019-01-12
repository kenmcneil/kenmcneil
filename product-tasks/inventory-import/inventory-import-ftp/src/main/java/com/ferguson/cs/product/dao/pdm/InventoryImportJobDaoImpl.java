package com.ferguson.cs.product.dao.pdm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.model.InventoryImportJobError;
import com.ferguson.cs.product.model.InventoryImportJobType;

@Repository
public class InventoryImportJobDaoImpl implements InventoryImportJobDao {

	private InventoryImportJobMapper inventoryImportJobMapper;

	@Autowired
	public void setInventoryImportJobMapper(InventoryImportJobMapper inventoryImportJobMapper) {
		this.inventoryImportJobMapper = inventoryImportJobMapper;
	}


	@Override
	public void insertFtpInventoryImportJobLog(FtpInventoryImportJobLog ftpInventoryImportJobLog) {
		if(ftpInventoryImportJobLog.getJobType() == null) {
			ftpInventoryImportJobLog.setJobType(InventoryImportJobType.FTP);
		}
		inventoryImportJobMapper.insertInventoryImportJobLog(ftpInventoryImportJobLog);
		if(!StringUtils.isBlank(ftpInventoryImportJobLog.getFileName())) {
			inventoryImportJobMapper.insertFtpInventoryImportJobDetails(ftpInventoryImportJobLog.getId(),ftpInventoryImportJobLog.getFileName(),ftpInventoryImportJobLog.getSftp());
		}

		for(InventoryImportJobError error : ftpInventoryImportJobLog.getErrors()) {
			error.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
			inventoryImportJobMapper.insertInventoryImportJobError(error);
		}
	}

	@Override
	public void updateFtpInventoryImportJobLog(FtpInventoryImportJobLog ftpInventoryImportJobLog) {
		if(ftpInventoryImportJobLog.getJobType() == null) {
			ftpInventoryImportJobLog.setJobType(InventoryImportJobType.FTP);
		}
		inventoryImportJobMapper.updateInventoryImportJobLog(ftpInventoryImportJobLog);
		for(InventoryImportJobError error : ftpInventoryImportJobLog.getErrors()) {
			if(error.getId() == null) {
				error.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
				inventoryImportJobMapper.insertInventoryImportJobError(error);
			}
		}
	}
}
