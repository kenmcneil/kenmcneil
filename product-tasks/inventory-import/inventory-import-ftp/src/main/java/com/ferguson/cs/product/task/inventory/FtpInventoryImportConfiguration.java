package com.ferguson.cs.product.task.inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ftp.gateway.FtpOutboundGateway;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.messaging.MessageHandler;
import org.springframework.retry.annotation.EnableRetry;

import com.ferguson.cs.product.task.inventory.model.VendorFtpData;

@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
@EnableRetry
public class FtpInventoryImportConfiguration {

	protected static final String INBOUND_SFTP_CHANNEL = "inboundSftpChannel";
	protected static final String INBOUND_FTP_CHANNEL = "inboundFtpChannel";

	private InventoryImportSettings inventoryImportSettings;

	@Autowired
	public void setInventoryImportSettings(InventoryImportSettings inventoryImportSettings) {
		this.inventoryImportSettings = inventoryImportSettings;
	}

	@Bean
	@ServiceActivator(inputChannel = INBOUND_SFTP_CHANNEL)
	public MessageHandler inboundSftpHandler() {
		SftpOutboundGateway sftpOutboundGateway = new SftpOutboundGateway(vendorFtpSessionFactory(), "get", "payload.ftpPath + payload.ftpFilename");
		sftpOutboundGateway.setLocalDirectory(new File(inventoryImportSettings.getInventoryDirectory()));
		sftpOutboundGateway.setLocalFilenameGeneratorExpressionString("payload.ftpFilename");
		sftpOutboundGateway.setAutoCreateLocalDirectory(true);
		sftpOutboundGateway.setFileExistsMode(FileExistsMode.REPLACE);
		List<Advice> adviceChain = new ArrayList<>();
		adviceChain.add(new RequestHandlerRetryAdvice());
		sftpOutboundGateway.setAdviceChain(adviceChain);
		return sftpOutboundGateway;
	}

	@Bean
	@ServiceActivator(inputChannel = INBOUND_FTP_CHANNEL)
	public MessageHandler inboundFtpHandler() {
		FtpOutboundGateway ftpOutboundGateway = new FtpOutboundGateway(vendorFtpSessionFactory(), "get", "payload.ftpFilename");
		ftpOutboundGateway.setLocalDirectory(new File(inventoryImportSettings.getInventoryDirectory()));
		ftpOutboundGateway.setLocalFilenameGeneratorExpressionString("payload.ftpFilename");
		ftpOutboundGateway.setFileExistsMode(FileExistsMode.REPLACE);
		ftpOutboundGateway.setAutoCreateLocalDirectory(true);
		ftpOutboundGateway.setWorkingDirExpressionString("payload.ftpPath");
		List<Advice> adviceChain = new ArrayList<>();
		adviceChain.add(new RequestHandlerRetryAdvice());
		ftpOutboundGateway.setAdviceChain(adviceChain);
		return ftpOutboundGateway;
	}

	@Bean
	public DelegatingSessionFactory vendorFtpSessionFactory() {
		return new DelegatingSessionFactory(vendorFtpSessionFactoryLocator());
	}

	@Bean
	public VendorFtpSessionFactoryLocator vendorFtpSessionFactoryLocator() {
		return new VendorFtpSessionFactoryLocator();
	}

	@MessagingGateway
	public interface InventoryGateway {

		@Gateway(requestChannel = INBOUND_SFTP_CHANNEL)
		File receiveVendorInventoryFileSftp(VendorFtpData vendorFtpData);

		@Gateway(requestChannel = INBOUND_FTP_CHANNEL)
		File receiveVendorInventoryFileFtp(VendorFtpData vendorFtpData);
	}
}
