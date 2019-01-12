package com.ferguson.cs.product.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.dao.pdm.InventoryImportJobDao;
import com.ferguson.cs.product.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.model.InventoryImportJobError;
import com.ferguson.cs.product.model.InventoryImportJobStatus;
import com.ferguson.cs.task.test.EnableTaskIntegrationTesting;
import com.ferguson.cs.test.BaseTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableTaskIntegrationTesting
@Transactional
public class InventoryImportJobDaoIT extends BaseTest{
	private InventoryImportJobDao inventoryImportJobDao;

	@Autowired
	public void setInventoryImportJobDao(InventoryImportJobDao inventoryImportJobDao) {
		this.inventoryImportJobDao = inventoryImportJobDao;
	}

	@Test
	public void testInsertFtpInventoryImportJobLog() {
		FtpInventoryImportJobLog ftpInventoryImportJobLog = new FtpInventoryImportJobLog();
		ftpInventoryImportJobLog.setStatus(InventoryImportJobStatus.FAILED);
		ftpInventoryImportJobLog.setSftp(false);
		ftpInventoryImportJobLog.setFileName("foo");
		ftpInventoryImportJobLog.setVendorUid(123);
		List<InventoryImportJobError> errors = new ArrayList<>();
		InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
		inventoryImportJobError.setErrorMessage("foobar");
		errors.add(inventoryImportJobError);
		ftpInventoryImportJobLog.setErrors(errors);
		inventoryImportJobDao.insertFtpInventoryImportJobLog(ftpInventoryImportJobLog);

		assertThat(ftpInventoryImportJobLog.getId()).isNotNull();
		assertThat(inventoryImportJobError.getId()).isNotNull();
		assertThat(inventoryImportJobError.getInventoryImportJobLogId()).isEqualTo(ftpInventoryImportJobLog.getId());
	}

	@Test
	public void testUpdateFtpInventoryImportJobLog() {
		FtpInventoryImportJobLog ftpInventoryImportJobLog = new FtpInventoryImportJobLog();
		ftpInventoryImportJobLog.setStatus(InventoryImportJobStatus.IN_PROGRESS);
		ftpInventoryImportJobLog.setSftp(false);
		ftpInventoryImportJobLog.setFileName("foo");
		ftpInventoryImportJobLog.setVendorUid(123);
		inventoryImportJobDao.insertFtpInventoryImportJobLog(ftpInventoryImportJobLog);

		List<InventoryImportJobError> errors = new ArrayList<>();
		InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
		inventoryImportJobError.setErrorMessage("foobar");
		errors.add(inventoryImportJobError);
		ftpInventoryImportJobLog.setErrors(errors);
		ftpInventoryImportJobLog.setLastUpdateDate(new Date());

		inventoryImportJobDao.updateFtpInventoryImportJobLog(ftpInventoryImportJobLog);

		assertThat(inventoryImportJobError.getId()).isNotNull();
		assertThat(inventoryImportJobError.getInventoryImportJobLogId()).isEqualTo(ftpInventoryImportJobLog.getId());
	}
}
