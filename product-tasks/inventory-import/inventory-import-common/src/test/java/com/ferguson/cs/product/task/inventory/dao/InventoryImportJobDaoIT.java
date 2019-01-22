package com.ferguson.cs.product.task.inventory.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.task.inventory.InventoryImportCommonConfiguration;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobEmailAttachment;
import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobError;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobStatus;
import com.ferguson.cs.task.test.EnableTaskIntegrationTesting;
import com.ferguson.cs.test.BaseTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,classes = InventoryImportCommonConfiguration.class)
@SpringBootApplication(scanBasePackages= {"com.ferguson.cs.product.task.inventory"})
@EnableTaskIntegrationTesting
@Transactional
public class InventoryImportJobDaoIT extends BaseTest{
	private InventoryImportJobDao inventoryImportJobDao;

	@TestConfiguration
	protected static class InventoryImportDaoITConfiguration {}

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

	@Test
	public void testInsertEmailInventoryImportJobLog() {
		EmailInventoryImportJobLog emailInventoryImportJobLog = new EmailInventoryImportJobLog();
		emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.IN_PROGRESS);
		List<InventoryImportJobError> errors = new ArrayList<>();
		InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
		inventoryImportJobError.setErrorMessage("foobar");
		errors.add(inventoryImportJobError);
		emailInventoryImportJobLog.setErrors(errors);

		inventoryImportJobDao.insertEmailInventoryImportJobLog(emailInventoryImportJobLog);

		InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment = new InventoryImportJobEmailAttachment();
		inventoryImportJobEmailAttachment.setWasSuccessful(true);
		inventoryImportJobEmailAttachment.setFilename("foo.txt");
		emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);

		assertThat(emailInventoryImportJobLog.getId()).isNotNull();
		assertThat(inventoryImportJobError.getId()).isNotNull();
		assertThat(inventoryImportJobError.getInventoryImportJobLogId()).isEqualTo(emailInventoryImportJobLog.getId());
		assertThat(emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().get(0).getInventoryImportJobLogId()).isEqualTo(emailInventoryImportJobLog.getId());
		assertThat(emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().get(0).getId()).isNotNull();
	}

	@Test
	public void testUpdateEmailInventoryImportJobLog() {
		EmailInventoryImportJobLog emailInventoryImportJobLog = new EmailInventoryImportJobLog();
		emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.IN_PROGRESS);
		List<InventoryImportJobError> errors = new ArrayList<>();
		InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
		inventoryImportJobError.setErrorMessage("foobar");
		errors.add(inventoryImportJobError);
		emailInventoryImportJobLog.setErrors(errors);

		inventoryImportJobDao.insertEmailInventoryImportJobLog(emailInventoryImportJobLog);

		assertThat(emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().isEmpty()).isTrue();

		InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment = new InventoryImportJobEmailAttachment();
		inventoryImportJobEmailAttachment.setWasSuccessful(true);
		inventoryImportJobEmailAttachment.setFilename("foo.txt");
		emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);

		emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.PARTIAL_FAILURE);

		inventoryImportJobDao.updateEmailInventoryImportJobLog(emailInventoryImportJobLog);
		assertThat(emailInventoryImportJobLog.getId()).isNotNull();
		assertThat(inventoryImportJobError.getId()).isNotNull();
		assertThat(inventoryImportJobError.getInventoryImportJobLogId()).isEqualTo(emailInventoryImportJobLog.getId());
		assertThat(emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().get(0)
				.getInventoryImportJobLogId()).isEqualTo(emailInventoryImportJobLog.getId());
		assertThat(emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().get(0).getId()).isNotNull();
	}
}