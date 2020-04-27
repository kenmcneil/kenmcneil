package com.ferguson.cs.product.task.dy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import com.jcraft.jsch.ChannelSftp;

@Configuration
public class DyFeedConfiguration {
	protected static final String CORE_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.dy.dao.core";
	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.dy.model";
	private final DyFeedSettings dyFeedSettings;
	private final Integer FTP_TIMEOUT = 600000;
	private static final Logger LOGGER = LoggerFactory.getLogger(DyFeedConfiguration.class);

	public DyFeedConfiguration(DyFeedSettings dyFeedSettings) {
		this.dyFeedSettings = dyFeedSettings;
	}

	@Bean
	MessageChannel sftpChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public SftpRemoteFileTemplate remoteTemplate() {
		SftpRemoteFileTemplate remoteTemplate = new SftpRemoteFileTemplate(sessionFactory());
		remoteTemplate.setFileNameGenerator(filename -> dyFeedSettings.getTempFilePrefix() +
				dyFeedSettings.getTempFileSuffix());
		remoteTemplate.setRemoteDirectoryExpression(new FunctionExpression<Message>(m -> m.getHeaders().get("remoteDirectory")));
		return remoteTemplate;
	}

	@Bean

	@Qualifier("sftpSessionFactory")
	public DelegatingSessionFactory<ChannelSftp.LsEntry> sessionFactory() {
		Map<Object, SessionFactory<ChannelSftp.LsEntry>> factories = new LinkedHashMap<>();

		Map<Integer, String> siteUsers = dyFeedSettings.getSiteUsername();

		for (Map.Entry<Integer, String> entry : siteUsers.entrySet()) {
			DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(false);
			factory.setHost(dyFeedSettings.getFtpUrl());
			factory.setUser(entry.getValue());
			factory.setPrivateKey(new ByteArrayResource(dyFeedSettings.getFtpPrivateKey().getBytes()));
			factory.setAllowUnknownKeys(true);
			factory.setTimeout(FTP_TIMEOUT);
			factories.put(entry.getKey(), factory);
		}
		// use the first SF as the default
		return new DelegatingSessionFactory<>(factories, factories.values().iterator().next());
	}


	@Bean
	IntegrationFlow sftpGateway(SftpRemoteFileTemplate template, DelegatingSessionFactory<ChannelSftp.LsEntry> dsf) {
		return f -> f.channel(sftpChannel())
				.handle((GenericHandler<Object>) ( file, messageHeaders) -> {
					dsf.setThreadKey(messageHeaders.get("siteId"));
					return file;
				}).handle(Sftp.outboundGateway(template, AbstractRemoteFileOutboundGateway.Command.PUT, "payload")
						.fileExistsMode(FileExistsMode.REPLACE)
				).handle((GenericHandler<Object>) (file, messageHeaders) -> {
					dsf.clearThreadKey();

					try {
						if (messageHeaders.get("tempFile") != null) {
							Files.delete(Paths.get(messageHeaders.get("tempFile").toString()));
						}
					} catch (IOException e) {
						LOGGER.warn("Failed to delete temporary file: " + messageHeaders.get("tempFile").toString(), e);
					}
					return null;
				});
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
