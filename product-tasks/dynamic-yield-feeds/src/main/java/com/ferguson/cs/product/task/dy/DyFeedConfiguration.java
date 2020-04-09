package com.ferguson.cs.product.task.dy;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;

import com.jcraft.jsch.ChannelSftp;

@Configuration
public class DyFeedConfiguration {
	private static final String CORE_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.dy.dao.core";
	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.dy.model";
	private static final String SFTP_OUTBOUND_CHANNEL = "dyOutboundChannel";
	private final DyFeedSettings dyFeedSettings;

	public DyFeedConfiguration(DyFeedSettings dyFeedSettings) {
		this.dyFeedSettings = dyFeedSettings;
	}

	@Bean
	public SftpRemoteFileTemplate remoteTemplate() {
		SftpRemoteFileTemplate remoteTemplate = new SftpRemoteFileTemplate(sessionFactory());
		remoteTemplate.setRemoteDirectoryExpression(new FunctionExpression<Message>(m -> m.getHeaders().get("remoteDirectory")));
		return remoteTemplate;
	}

	@Bean
	public DelegatingSessionFactory<ChannelSftp.LsEntry> sessionFactory() {
		Map<Object, SessionFactory<ChannelSftp.LsEntry>> factories = new LinkedHashMap<>();

		Map<Integer, String> siteUsers = dyFeedSettings.getSiteUsername();

		for (Map.Entry<Integer, String> entry : siteUsers.entrySet()) {
			DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(false);
			factory.setHost(dyFeedSettings.getFtpUrl());
			factory.setUser(entry.getValue());
			factory.setPrivateKey(new ByteArrayResource(dyFeedSettings.getFtpPrivateKey().getBytes()));
			factory.setAllowUnknownKeys(true);
			factories.put(entry.getKey(), factory);
		}
		// use the first SF as the default
		return new DelegatingSessionFactory<>(factories, factories.values().iterator().next());
	}

	@ServiceActivator(inputChannel = SFTP_OUTBOUND_CHANNEL)
	@Bean
	public SftpMessageHandler handler() {
		SftpMessageHandler handler = new SftpMessageHandler(remoteTemplate());
		return handler;
	}

	@MessagingGateway(defaultRequestChannel = SFTP_OUTBOUND_CHANNEL)
	public interface SftpGateway {
		void send(File file, @Header("remoteDirectory") String remoteDirectory);
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
}
