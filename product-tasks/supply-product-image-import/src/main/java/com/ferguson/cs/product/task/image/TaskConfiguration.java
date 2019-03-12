package com.ferguson.cs.product.task.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@Configuration
public class TaskConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskConfiguration.class);

	@Bean
	@ConfigurationProperties(prefix = "supply-image-import-ftp")
	public SupplyImageImportFtpConfiguration supplyImageImportFtp() {
		return new SupplyImageImportFtpConfiguration();
	}

	@Bean
	public SessionFactory<LsEntry> supplySessionFactory() {
		final SupplyImageImportFtpConfiguration supplyFtp = supplyImageImportFtp();
		final DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		LOGGER.info("Supply ftp session factory initializing .... " + System.lineSeparator() + System.lineSeparator()
				+ supplyFtp.toString() + System.lineSeparator());
		factory.setHost(supplyFtp.getFtpHost());
		factory.setPort(supplyFtp.getFtpPort());
		factory.setUser(supplyFtp.getUserId());
		factory.setPassword(supplyFtp.getPassword());
		factory.setAllowUnknownKeys(true);

		return factory;
	}

}
