package com.ferguson.cs.product.task.supply;

import java.io.File;
import javax.sql.DataSource;
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
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.messaging.MessageHandler;
import com.jcraft.jsch.ChannelSftp;


@Configuration
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.supply")
public class SupplyProductDataFeedConfiguration {

	protected static final String BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.supplu.dao";
	protected static final String BASE_ALIAS_PAKCAGE = "com.ferguson.cs.product.task.supply.model";
	protected static final String SUPPLY_SFTP_CHANNEL = "supplySftpChannel";
	public static final int SFTP_PORT = 22;

	private SupplyProductDataFeedSettings supplyProductDataFeedSettings;

	@Autowired
	public void setSupplyProductDataFeedSettings(SupplyProductDataFeedSettings supplyProductDataFeedSettings) {
		this.supplyProductDataFeedSettings = supplyProductDataFeedSettings;
	}


	@MapperScan(basePackages= SupplyProductDataFeedConfiguration.BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "sqlSessionFactory")
	@Configuration
	public static class DataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@Primary
		@ConfigurationProperties(prefix = "datasource.core")
		public DataSourceProperties reporterDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@Primary
		@ConfigurationProperties(prefix = "datasource.core")
		public DataSource reporterDataSource() {
			return reporterDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		public DataSourceTransactionManager reporterTransactionManager() {
			return new DataSourceTransactionManager(reporterDataSource());
		}

		@Bean
		public SqlSessionFactory reporterSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(reporterDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@Bean(name = "supplyFtpSessionFactory")
	public SessionFactory<ChannelSftp.LsEntry> supplySftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(supplyProductDataFeedSettings.getFtpUrl());
		factory.setPort(supplyProductDataFeedSettings.getFtpPort());
		factory.setUser(supplyProductDataFeedSettings.getFtpUser());
		factory.setPassword(supplyProductDataFeedSettings.getFtpPassword());
		factory.setAllowUnknownKeys(true);

		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = SUPPLY_SFTP_CHANNEL)
	public MessageHandler supplySftpHandler() {
		SftpMessageHandler handler = new SftpMessageHandler(supplySftpSessionFactory());
		handler.setRemoteDirectoryExpression(new LiteralExpression(supplyProductDataFeedSettings.getFtpPath()));
		handler.setUseTemporaryFileName(false);
		handler.setFileNameGenerator(message -> ((File)message.getPayload()).getName());
		return handler;
	}

	@MessagingGateway
	public interface SupplyGateway {
		@Gateway(requestChannel = SUPPLY_SFTP_CHANNEL)
		void sendSupplyFileSftp(File file);
	}


}
