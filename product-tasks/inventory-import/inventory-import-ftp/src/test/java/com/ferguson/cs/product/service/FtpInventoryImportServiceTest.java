package com.ferguson.cs.product.service;

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

import com.ferguson.cs.product.InventoryImportCommonConfiguration;
import com.ferguson.cs.product.InventoryImportCommonConfiguration.InventoryGateway;
import com.ferguson.cs.product.dao.pdm.InventoryImportJobDao;
import com.ferguson.cs.product.dao.reporter.FtpInventoryDao;
import com.ferguson.cs.product.model.FtpInventoryImportJobLog;
import com.ferguson.cs.product.model.InventoryImportJobStatus;
import com.ferguson.cs.product.model.VendorFtpData;
import com.ferguson.cs.product.service.inventory.FtpInventoryImportService;
import com.ferguson.cs.product.service.inventory.FtpInventoryImportServiceImpl;
import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.ValueUtils;

public class FtpInventoryImportServiceTest extends BaseTest {
	@Mock
	private FtpInventoryDao ftpInventoryDao;

	@Mock
	private InventoryImportJobDao inventoryImportJobDao;

	@Mock
	private DelegatingSessionFactory delegatingSessionFactory;

	@Mock
	private InventoryGateway inventoryGateway;

	@InjectMocks
	@Spy
	private FtpInventoryImportService ftpInventoryImportService = new FtpInventoryImportServiceImpl();

	@Before
	public void beforeTest() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testDownloadVendorInventoryFiles_ftp_success() {
		VendorFtpData vendorFtpData = ValueUtils.getRandomValue(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.FTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		ftpInventoryImportService.downloadVendorInventoryFiles();
		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);


		verify(inventoryGateway,times(1)).receiveVendorInventoryFileFtp(any());
		verify(ftpInventoryImportService,times(2)).saveFtpInventoryImportJobLog(argument.capture());

		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.COMPLETE);
		assertThat(ftpInventoryImportJobLog.getErrors()).isEmpty();

	}

	@Test
	public void testDownloadVendorInventoryFiles_ftp_failure() {
		VendorFtpData vendorFtpData = ValueUtils.getRandomValue(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.FTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		MessagingException e = new MessagingException("Failure", new MessagingException("Failure Cause"));
		when(inventoryGateway.receiveVendorInventoryFileFtp(any())).thenThrow(e);

		ftpInventoryImportService.downloadVendorInventoryFiles();


		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);

		verify(inventoryGateway,times(1)).receiveVendorInventoryFileFtp(any());
		verify(ftpInventoryImportService,times(2)).saveFtpInventoryImportJobLog(argument.capture());
		assertThat(argument.getAllValues().size()).isEqualTo(2);
		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.FAILED);
		assertThat(ftpInventoryImportJobLog.getErrors()).hasSize(1);
		assertThat(ftpInventoryImportJobLog.getErrors().get(0).getErrorMessage()).contains("Failure Cause");

	}

	@Test
	public void testDownloadVendorInventoryFiles_sftp_success() {
		VendorFtpData vendorFtpData = ValueUtils.getRandomValue(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.SFTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		ftpInventoryImportService.downloadVendorInventoryFiles();

		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);

		verify(inventoryGateway,times(1)).receiveVendorInventoryFileSftp(any());
		verify(ftpInventoryImportService,times(2)).saveFtpInventoryImportJobLog(argument.capture());

		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.COMPLETE);
		assertThat(ftpInventoryImportJobLog.getErrors()).isEmpty();
	}

	@Test
	public void testDownloadVendorInventoryFiles_sftp_failure() {
		VendorFtpData vendorFtpData = ValueUtils.getRandomValue(VendorFtpData.class);
		vendorFtpData.setFtpPort(InventoryImportCommonConfiguration.SFTP_PORT);
		List<VendorFtpData> vendorFtpDataList = new ArrayList<>();
		vendorFtpDataList.add(vendorFtpData);
		when(ftpInventoryDao.getVendorFtpData()).thenReturn(vendorFtpDataList);
		MessagingException e = new MessagingException("Failure", new MessagingException("Failure Cause"));
		when(inventoryGateway.receiveVendorInventoryFileSftp(any())).thenThrow(e);

		ftpInventoryImportService.downloadVendorInventoryFiles();


		ArgumentCaptor<FtpInventoryImportJobLog> argument =  ArgumentCaptor.forClass(FtpInventoryImportJobLog.class);

		verify(inventoryGateway,times(1)).receiveVendorInventoryFileSftp(any());
		verify(ftpInventoryImportService,times(2)).saveFtpInventoryImportJobLog(argument.capture());
		assertThat(argument.getAllValues().size()).isEqualTo(2);
		FtpInventoryImportJobLog ftpInventoryImportJobLog = argument.getAllValues().get(1);

		assertThat(ftpInventoryImportJobLog.getStatus()).isEqualTo(InventoryImportJobStatus.FAILED);
		assertThat(ftpInventoryImportJobLog.getErrors()).hasSize(1);
		assertThat(ftpInventoryImportJobLog.getErrors().get(0).getErrorMessage()).contains("Failure Cause");

	}

}
