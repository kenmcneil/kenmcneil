package com.ferguson.cs.product.task.inventory.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.inventory.model.InventoryImportJobEmailAttachment;
import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobError;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobType;


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



	@Override
	public void insertEmailInventoryImportJobLog(EmailInventoryImportJobLog emailInventoryImportJobLog) {
		if(emailInventoryImportJobLog.getJobType() == null) {
			emailInventoryImportJobLog.setJobType(InventoryImportJobType.EMAIL);
		}
		inventoryImportJobMapper.insertInventoryImportJobLog(emailInventoryImportJobLog);

		for (InventoryImportJobEmailAttachment attachmentLog : emailInventoryImportJobLog
				.getInventoryImportJobEmailAttachmentList()) {
			if (attachmentLog.getInventoryImportJobLogId() == null) {
				attachmentLog.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
			}
			if (attachmentLog.getInventoryImportJobLogId().equals(emailInventoryImportJobLog.getId())) {
				inventoryImportJobMapper.insertInventoryImportJobEmailAttachment(attachmentLog);
			}
		}


		for (InventoryImportJobError error : emailInventoryImportJobLog.getErrors()) {
			error.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
			inventoryImportJobMapper.insertInventoryImportJobError(error);
		}
	}

	@Override
	public void updateEmailInventoryImportJobLog(EmailInventoryImportJobLog emailInventoryImportJobLog) {
		if(emailInventoryImportJobLog.getJobType() == null) {
			emailInventoryImportJobLog.setJobType(InventoryImportJobType.EMAIL);
		}
		inventoryImportJobMapper.updateInventoryImportJobLog(emailInventoryImportJobLog);

		for (InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment : emailInventoryImportJobLog
				.getInventoryImportJobEmailAttachmentList()) {
			if (inventoryImportJobEmailAttachment.getId() != null) {
				continue;
			}
			if (inventoryImportJobEmailAttachment.getInventoryImportJobLogId() == null) {
				inventoryImportJobEmailAttachment.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
			}
			if (inventoryImportJobEmailAttachment.getInventoryImportJobLogId().equals(emailInventoryImportJobLog.getId())) {
				inventoryImportJobMapper.insertInventoryImportJobEmailAttachment(inventoryImportJobEmailAttachment);
			}
		}

		for (InventoryImportJobError error : emailInventoryImportJobLog.getErrors()) {
			if (error.getId() == null) {
				error.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
				inventoryImportJobMapper.insertInventoryImportJobError(error);
			}
		}
	}
}
