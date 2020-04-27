package com.ferguson.cs.product.task.feipricefeed;

import java.io.File;

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
public class FeiPriceConfiguration {

	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.feipricefeed.model";
	private static final String BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.feipricefeed.data";
	private static final String FEI_UPLOAD_SFTP_CHANNEL = "feiUploadSftpChannel";
	private final FeiPriceSettings feiPriceSettings;

	public FeiPriceConfiguration(FeiPriceSettings feiPriceSettings) {
		this.feiPriceSettings = feiPriceSettings;
	}

	@MapperScan(basePackages = FeiPriceConfiguration.BASE_MAPPER_PACKAGE, annotationClass = Mapper.class, sqlSessionFactoryRef = "reporterSqlSessionFactory")
	@Configuration
	protected static class ReporterDataSourceConfiguration {
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
			factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@Bean(name = "feiFtpSession")
	public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(feiPriceSettings.getFtpUrl());
		factory.setPort(feiPriceSettings.getFtpPort());
		factory.setUser(feiPriceSettings.getFtpUsername());
		factory.setPassword(feiPriceSettings.getFtpPassword());
		factory.setAllowUnknownKeys(true);
		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = FEI_UPLOAD_SFTP_CHANNEL)
	public MessageHandler feiSftpHandler() {
		SftpMessageHandler handler = new SftpMessageHandler((sftpSessionFactory()));
		handler.setRemoteDirectoryExpression(new LiteralExpression(feiPriceSettings.getFtpFolder()));
		handler.setUseTemporaryFileName(false);
		handler.setFileNameGenerator(message -> ((File)message.getPayload()).getName());
		return handler;
	}

	@MessagingGateway
	public interface FeiGateway {
		@Gateway(requestChannel = FEI_UPLOAD_SFTP_CHANNEL)
		void sendFeiFileSftp(File file);
	}
}
