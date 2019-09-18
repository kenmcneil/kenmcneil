package com.ferguson.cs.product.task.wiser;

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
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;

import com.ferguson.cs.product.task.wiser.model.FileDownloadRequest;
import com.jcraft.jsch.ChannelSftp;

@Configuration
@IntegrationComponentScan
public class WiserFeedConfiguration {
	protected static final String REPORTER_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.wiser.dao.reporter";
	public static final String INTEGRATION_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.wiser.dao.integration";
	protected static final String BATCH_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.wiser.dao.batch";
	private static final String BASE_ALIAS_PAKCAGE = "com.ferguson.cs.product.task.wiser.model";
	private static final String WISER_OUTBOUND_SFTP_CHANNEL = "wiserOutboundSftpChannel";
	private static final String THREE_SIXTY_PI_SFTP_CHANNEL = "threeSixtyPiSftpChannel";
	private static final String THREE_SIXTY_PI_DELETE_SFTP_CHANNEL = "threeSixtyPiDeleteSftpChannel";

	private WiserFeedSettings wiserFeedSettings;
	private ThreeSixtyPiSettings threeSixtyPiSettings;

	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
	}

	@Autowired
	public void setThreeSixtyPiSettings(ThreeSixtyPiSettings threeSixtyPiSettings) {
		this.threeSixtyPiSettings = threeSixtyPiSettings;
	}

	@Bean(name = "wiserFtpSession")
	public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(wiserFeedSettings.getFtpUrl());
		factory.setPort(wiserFeedSettings.getFtpPort());
		factory.setUser(wiserFeedSettings.getFtpUsername());
		factory.setPassword(wiserFeedSettings.getFtpPassword());
		factory.setAllowUnknownKeys(true);
		return factory;
	}

	@Bean(name = "360piFtpSession")
	public SessionFactory<ChannelSftp.LsEntry> threeSixtyPiSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(threeSixtyPiSettings.getFtpUrl());
		factory.setPort(threeSixtyPiSettings.getFtpPort());
		factory.setUser(threeSixtyPiSettings.getFtpUsername());
		factory.setPassword(threeSixtyPiSettings.getFtpPassword());
		factory.setAllowUnknownKeys(true);

		return factory;
	}


	@Bean
	@ServiceActivator(inputChannel = WISER_OUTBOUND_SFTP_CHANNEL)
	public MessageHandler wiserSftpHandler() {
		SftpMessageHandler handler = new SftpMessageHandler((sftpSessionFactory()));
		handler.setRemoteDirectoryExpression(new LiteralExpression(wiserFeedSettings.getFtpFolder()));
		handler.setUseTemporaryFileName(false);
		handler.setFileNameGenerator(message -> ((File)message.getPayload()).getName());
		return handler;
	}

	@Bean
	@ServiceActivator(inputChannel = THREE_SIXTY_PI_SFTP_CHANNEL)
	public MessageHandler threeSixtyPiSftpHandler() {
		SftpOutboundGateway sftpOutboundGateway = new SftpOutboundGateway(threeSixtyPiSessionFactory(),"get","payload.remoteFilePath");
		sftpOutboundGateway.setLocalDirectory(new File(wiserFeedSettings.getLocalFilePath()));
		sftpOutboundGateway.setLocalFilenameGeneratorExpressionString("payload.localFilePath");
		sftpOutboundGateway.setAutoCreateLocalDirectory(true);
		return  sftpOutboundGateway;
	}

	@Bean
	@ServiceActivator(inputChannel = THREE_SIXTY_PI_DELETE_SFTP_CHANNEL)
	public MessageHandler threeSixtyPiDeleteSftpHandler() {
		SftpOutboundGateway sftpOutboundGateway = new SftpOutboundGateway(threeSixtyPiSessionFactory(),"rm","payload");
		sftpOutboundGateway.setLocalDirectory(new File(wiserFeedSettings.getLocalFilePath()));
		sftpOutboundGateway.setAutoCreateLocalDirectory(true);
		return  sftpOutboundGateway;
	}

	@MapperScan(basePackages= WiserFeedConfiguration.BATCH_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "batchSqlSessionFactory")
	@Configuration
	public static class BatchDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.batch")
		public DataSourceProperties batchDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "datasource.batch")
		public DataSource batchDataSource() {
			return batchDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		public SqlSessionFactory batchSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(batchDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MapperScan(basePackages= WiserFeedConfiguration.REPORTER_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "reporterSqlSessionFactory")
	@Configuration
	public static class ReporterDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSourceProperties reporterDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSource reporterDataSource() {
			return reporterDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		@Primary
		public SqlSessionFactory reporterSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(reporterDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MapperScan(basePackages= WiserFeedConfiguration.INTEGRATION_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "integrationSqlSessionFactory")
	@Configuration
	public static class IntegrationDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the integration data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.integration")
		public DataSourceProperties integrationDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "datasource.integration")
		public DataSource integrationDataSource() {
			return integrationDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		public SqlSessionFactory integrationSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(integrationDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MessagingGateway
	public interface WiserGateway {
		@Gateway(requestChannel = WISER_OUTBOUND_SFTP_CHANNEL)
		void sendWiserFileSftp(File file);

		@Gateway(requestChannel = THREE_SIXTY_PI_SFTP_CHANNEL)
		File receive360piFileSftp(FileDownloadRequest fileDownloadRequest);

		@Gateway(requestChannel = THREE_SIXTY_PI_DELETE_SFTP_CHANNEL)
		Object deleteWiserFileSftp(String remoteFilePath);
	}
}
