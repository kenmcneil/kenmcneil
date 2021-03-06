package com.ferguson.cs.product.task.inventory.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.FtpInventoryImportConfiguration.InventoryGateway;
import com.ferguson.cs.product.task.inventory.InventoryImportCommonConfiguration;
import com.ferguson.cs.product.task.inventory.dao.reporter.FtpInventoryDao;
import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobError;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobErrorMessage;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobStatus;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobType;
import com.ferguson.cs.product.task.inventory.model.VendorFtpData;

@Service("ftpInventoryImportService")
public class FtpInventoryImportServiceImpl implements InventoryImportService {
	private FtpInventoryDao ftpInventoryDao;
	private InventoryImportJobLogService inventoryImportJobLogService;
	@SuppressWarnings("rawtypes")
	private DelegatingSessionFactory delegatingSessionFactory;
	private InventoryGateway inventoryGateway;

	private static final Logger LOG = LoggerFactory.getLogger(FtpInventoryImportServiceImpl.class);

	@Autowired
	public void setFtpInventoryDao(FtpInventoryDao ftpInventoryDao) {
		this.ftpInventoryDao = ftpInventoryDao;
	}

	@Autowired
	public void setInventoryImportJobLogService(InventoryImportJobLogService inventoryImportJobLogService) {
		this.inventoryImportJobLogService = inventoryImportJobLogService;
	}

	@Autowired
	public void setDelegatingSessionFactory(@SuppressWarnings("rawtypes") DelegatingSessionFactory delegatingSessionFactory) {
		this.delegatingSessionFactory = delegatingSessionFactory;
	}

	@Autowired
	public void setInventoryGateway(InventoryGateway inventoryGateway) {
		this.inventoryGateway = inventoryGateway;
	}

	@Override
	public void importInventory() {
		List<VendorFtpData> vendorFtpDataList = ftpInventoryDao.getVendorFtpData();

		for (VendorFtpData vendorFtpData : vendorFtpDataList) {
			FtpInventoryImportJobLog ftpInventoryImportJobLog = new FtpInventoryImportJobLog();
			ftpInventoryImportJobLog.setJobType(InventoryImportJobType.FTP);
			ftpInventoryImportJobLog.setVendorUid(vendorFtpData.getUid());
			ftpInventoryImportJobLog.setFileName(vendorFtpData.getFtpFilename());
			ftpInventoryImportJobLog.setStatus(InventoryImportJobStatus.IN_PROGRESS);

			if (vendorFtpData.getFtpPort() == InventoryImportCommonConfiguration.FTP_PORT) {
				ftpInventoryImportJobLog.setSftp(false);
			} else {
				ftpInventoryImportJobLog.setSftp(true);
			}
			inventoryImportJobLogService.saveInventoryImportJobLog(ftpInventoryImportJobLog);

			if (StringUtils.isBlank(vendorFtpData.getFtpUrl())) {
				InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
				inventoryImportJobError
						.setErrorMessage(InventoryImportJobErrorMessage.MISSING_FTP_URL.getStringValue());
				inventoryImportJobError.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
				ftpInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			}

			if (StringUtils.isBlank(vendorFtpData.getFtpFilename())) {
				InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
				inventoryImportJobError
						.setErrorMessage(InventoryImportJobErrorMessage.MISSING_FTP_FILENAME.getStringValue());
				inventoryImportJobError.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
				ftpInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			}

			if (StringUtils.isBlank(vendorFtpData.getFtpUser())) {
				InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
				inventoryImportJobError
						.setErrorMessage(InventoryImportJobErrorMessage.MISSING_FTP_USER.getStringValue());
				inventoryImportJobError.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
				ftpInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			}

			if (ftpInventoryImportJobLog.getErrors().isEmpty()) {

				delegatingSessionFactory.setThreadKey(vendorFtpData);
				if (vendorFtpData.getFtpPort() == null) {
					vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.SFTP_PORT);
				}
				if (ftpInventoryImportJobLog.getSftp()) {
					try {

						receiveVendorInventoryFileSftp(vendorFtpData);
					} catch (Exception e) {
						InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
						inventoryImportJobError.setErrorMessage(StringUtils.truncate(String
								.format(InventoryImportJobErrorMessage.SFTP_FILE_TRANSFER_ERROR
										.getStringValue(), e.getCause().toString()),255));
						inventoryImportJobError.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
						ftpInventoryImportJobLog.getErrors().add(inventoryImportJobError);
					}
				} else {
					try {
						receiveVendorInventoryFileFtp(vendorFtpData);
					} catch (Exception e) {
						InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
						inventoryImportJobError.setErrorMessage(StringUtils.truncate(String
								.format(InventoryImportJobErrorMessage.FTP_FILE_TRANSFER_ERROR
										.getStringValue(), e.getCause().toString()),255));
						inventoryImportJobError.setInventoryImportJobLogId(ftpInventoryImportJobLog.getId());
						ftpInventoryImportJobLog.getErrors().add(inventoryImportJobError);
					}
				}
				delegatingSessionFactory.clearThreadKey();
			}
			if (ftpInventoryImportJobLog.getErrors().isEmpty()) {
				ftpInventoryImportJobLog.setStatus(InventoryImportJobStatus.COMPLETE);
			} else {
				ftpInventoryImportJobLog.setStatus(InventoryImportJobStatus.FAILED);
			}
			inventoryImportJobLogService.saveInventoryImportJobLog(ftpInventoryImportJobLog);
		}
	}

	@Retryable
	private void receiveVendorInventoryFileSftp(VendorFtpData vendorFtpData) {
		LOG.info("Retrieving file from " + vendorFtpData.getVendorId());
		inventoryGateway.receiveVendorInventoryFileSftp(vendorFtpData);
		LOG.info("Retrieved file from " + vendorFtpData.getVendorId());
	}

	@Retryable
	private void receiveVendorInventoryFileFtp(VendorFtpData vendorFtpData) {
		LOG.info("Retrieving file from " + vendorFtpData.getVendorId());
		inventoryGateway.receiveVendorInventoryFileFtp(vendorFtpData);
		LOG.info("Retrieved file from " + vendorFtpData.getVendorId());
	}
}
