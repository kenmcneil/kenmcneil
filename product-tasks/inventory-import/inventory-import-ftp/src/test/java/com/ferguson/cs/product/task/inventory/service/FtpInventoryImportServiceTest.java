package com.ferguson.cs.product.task.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.messaging.MessagingException;

import com.ferguson.cs.product.task.inventory.FtpInventoryImportConfiguration;
import com.ferguson.cs.product.task.inventory.InventoryImportCommonConfiguration;
import com.ferguson.cs.product.task.inventory.dao.reporter.FtpInventoryDao;
import com.ferguson.cs.product.task.inventory.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobStatus;
import com.ferguson.cs.product.task.inventory.model.VendorFtpData;
import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.random.RandomUtils;

public class FtpInventoryImportServiceTest extends BaseTest {
	@Mock
	private FtpInventoryDao ftpInventoryDao;

	@Mock
	private InventoryImportJobLogService inventoryImportJobLogService;

	@SuppressWarnings("rawtypes")
	@Mock
	private DelegatingSessionFactory delegatingSessionFactory;

	@Mock
	private FtpInventoryImportConfiguration.InventoryGateway inventoryGateway;

	@InjectMocks
	@Spy
	private InventoryImportService ftpInventoryImportService = new FtpInventoryImportServiceImpl();

	@Before
	public void beforeTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testDownloadVendorInventoryFiles_ftp_success() {
		VendorFtpData vendorFtpData = RandomUtils.randomInstance(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.FTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		ftpInventoryImportService.importInventory();
		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);


		verify(inventoryGateway,times(1)).receiveVendorInventoryFileFtp(any());
		verify(inventoryImportJobLogService,times(2)).saveInventoryImportJobLog(argument.capture());

		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.COMPLETE);
		assertThat(ftpInventoryImportJobLog.getErrors()).isEmpty();

	}

	@Test
	public void testDownloadVendorInventoryFiles_ftp_failure() {
		VendorFtpData vendorFtpData = RandomUtils.randomInstance(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.FTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		MessagingException e = new MessagingException("Failure", new MessagingException("Failure Cause"));
		when(inventoryGateway.receiveVendorInventoryFileFtp(any())).thenThrow(e);

		ftpInventoryImportService.importInventory();


		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);

		verify(inventoryGateway,times(1)).receiveVendorInventoryFileFtp(any());
		verify(inventoryImportJobLogService,times(2)).saveInventoryImportJobLog(argument.capture());
		assertThat(argument.getAllValues().size()).isEqualTo(2);
		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.FAILED);
		assertThat(ftpInventoryImportJobLog.getErrors()).hasSize(1);
		assertThat(ftpInventoryImportJobLog.getErrors().get(0).getErrorMessage()).contains("Failure Cause");

	}

	@Test
	public void testDownloadVendorInventoryFiles_sftp_success() {
		VendorFtpData vendorFtpData = RandomUtils.randomInstance(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.SFTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		ftpInventoryImportService.importInventory();

		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);

		verify(inventoryGateway,times(1)).receiveVendorInventoryFileSftp(any());
		verify(inventoryImportJobLogService,times(2)).saveInventoryImportJobLog(argument.capture());

		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.COMPLETE);
		assertThat(ftpInventoryImportJobLog.getErrors()).isEmpty();
	}

	@Test
	public void testDownloadVendorInventoryFiles_sftp_failure() {
		VendorFtpData vendorFtpData = RandomUtils.randomInstance(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.SFTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		MessagingException e = new MessagingException("Failure", new MessagingException("Failure Cause"));
		when(inventoryGateway.receiveVendorInventoryFileSftp(any())).thenThrow(e);

		ftpInventoryImportService.importInventory();


		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);

		verify(inventoryGateway,times(1)).receiveVendorInventoryFileSftp(any());
		verify(inventoryImportJobLogService,times(2)).saveInventoryImportJobLog(argument.capture());
		assertThat(argument.getAllValues().size()).isEqualTo(2);
		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.FAILED);
		assertThat(ftpInventoryImportJobLog.getErrors()).hasSize(1);
		assertThat(ftpInventoryImportJobLog.getErrors().get(0).getErrorMessage()).contains("Failure Cause");

	}

}
