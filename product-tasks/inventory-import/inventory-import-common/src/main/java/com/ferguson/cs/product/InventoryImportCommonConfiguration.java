package com.ferguson.cs.product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.aopalliance.aop.Advice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ftp.gateway.FtpOutboundGateway;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.messaging.MessageHandler;

import com.ferguson.cs.product.model.VendorFtpData;

@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product")
public class InventoryImportCommonConfiguration {

	protected static final String REPORTER_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.dao.reporter";
	protected static final String PDM_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.dao.pdm";
	protected static final String BASE_ALIAS_PAKCAGE = "com.ferguson.cs.product.model";
	protected static final String INBOUND_SFTP_CHANNEL = "inboundSftpChannel";
	protected static final String INBOUND_FTP_CHANNEL = "inboundFtpChannel";
	public static final int FTP_PORT = 21;
	public static final int SFTP_PORT = 22;

	private InventoryImportSettings inventoryImportSettings;

	@Autowired
	public void setInventoryImportSettings(InventoryImportSettings inventoryImportSettings) {
		this.inventoryImportSettings = inventoryImportSettings;
	}


	@MapperScan(basePackages= InventoryImportCommonConfiguration.REPORTER_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "reporterSqlSessionFactory")
	@Configuration
	public static class ReporterDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean(name = {"reporterDataSourceProperties"}, destroyMethod="")
		@Primary
		@ConfigurationProperties(prefix = "datasource.reporter")
		public DataSourceProperties inventorioReporterDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean(name = {"reporterDataSource"}, destroyMethod="")
		@Primary
		@ConfigurationProperties(prefix = "datasource.reporter")
		public DataSource reporterDataSource() {
			return inventorioReporterDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean(name = "reporterTransactionManager")
		public DataSourceTransactionManager reporterTransactionManager() {
			return new DataSourceTransactionManager(reporterDataSource());
		}

		@Bean(name = "reporterSqlSessionFactory")
		public SqlSessionFactory reporterSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(reporterDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MapperScan(basePackages= InventoryImportCommonConfiguration.PDM_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "pdmSqlSessionFactory")
	@Configuration
	public static class PdmDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the pdm data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean(name = {"pdmDataSourceProperties"}, destroyMethod="")
		@ConfigurationProperties(prefix = "datasource.pdm")
		public DataSourceProperties inventorioPdmDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean(name = {"pdmDataSource"}, destroyMethod="")
		@ConfigurationProperties(prefix = "datasource.pdm")
		public DataSource pdmDataSource() {
			return inventorioPdmDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean(name = "pdmTransactionManager")
		@Primary
		public DataSourceTransactionManager pdmTransactionManager() {
			return new DataSourceTransactionManager(pdmDataSource());
		}

		@Bean(name = "pdmSqlSessionFactory")
		@Primary
		public SqlSessionFactory pdmSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(pdmDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@Bean
	@ServiceActivator(inputChannel = INBOUND_SFTP_CHANNEL)
	public MessageHandler inboundSftpHandler() {
		SftpOutboundGateway sftpOutboundGateway = new SftpOutboundGateway(vendorFtpSessionFactory(),"get","payload.ftpPath + payload.ftpFilename");
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
	public MessageHandler  inboundFtpHandler() {
		FtpOutboundGateway ftpOutboundGateway = new FtpOutboundGateway(vendorFtpSessionFactory(),"get","payload.ftpFilename");
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
