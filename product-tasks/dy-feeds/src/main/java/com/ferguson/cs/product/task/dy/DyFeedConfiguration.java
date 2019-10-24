package com.ferguson.cs.product.task.dy;

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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;

import com.jcraft.jsch.ChannelSftp;

@Configuration
@IntegrationComponentScan
public class DyFeedConfiguration {
	protected static final String CORE_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.dy.dao.core";
	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.dy.model";
	private static final String DY_SFTP_SESSION = "dySftpSession";
	private static final String DY_UPLOAD_SFTP_CHANNEL = "dyUploadSftpChannel";

	private DyFeedSettings dyFeedSettings;

	@Autowired
	public void setDyFeedSettings(DyFeedSettings dyFeedSettings) {
		this.dyFeedSettings = dyFeedSettings;
	}

	@Bean(name = DY_SFTP_SESSION)
	public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory()  {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(dyFeedSettings.getFtpUrl());
		factory.setPort(dyFeedSettings.getFtpPort());
		factory.setUser(dyFeedSettings.getFtpUsername());
		factory.setPrivateKey(new ByteArrayResource(dyFeedSettings.getFtpPrivateKey().getBytes()));
		factory.setAllowUnknownKeys(true);
		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = DY_UPLOAD_SFTP_CHANNEL)
	public MessageHandler dySftpHandler()  {
		SftpMessageHandler handler = new SftpMessageHandler((sftpSessionFactory()));
		handler.setRemoteDirectoryExpression(new LiteralExpression(dyFeedSettings.getFtpRoot()
				+ dyFeedSettings.getFtpUsername()));
		handler.setUseTemporaryFileName(false);
		handler.setFileNameGenerator(message -> dyFeedSettings.getTempFilePrefix() + '.' + dyFeedSettings.getTempFileSuffix());
		return handler;
	}

	@MapperScan(basePackages= DyFeedConfiguration.CORE_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "coreSqlSessionFactory")
	@Configuration
	public static class CoreDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the core data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSourceProperties coreDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSource coreDataSource() {
			return coreDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		@Primary
		public SqlSessionFactory coreSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(coreDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MessagingGateway
	public interface DynamicYieldGateway {
		@Gateway(requestChannel = DY_UPLOAD_SFTP_CHANNEL)
		void sendDyFileSftp(File file);
	}
}
