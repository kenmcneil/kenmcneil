package com.ferguson.cs.product.task.inventory;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@IntegrationComponentScan(basePackages = "com.ferguson.cs.product.task.inventory")
public class ManhattanInventoryProcessorConfiguration {

	private static final String MANHATTAN_SUPPLY_SFTP_CHANNEL = "ManhattanSupplySftpChannel";
	private static final String MANHATTAN_HMWALLACE_SFTP_CHANNEL = "ManhattanHmWallaceSftpChannel";

	private ManhattanInboundSettings manhattanInboundSettings;

	@Autowired
	public void setManhattanInboundSettings(ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@Bean(name = "supplyFtpSessionFactory")
	public SessionFactory<ChannelSftp.LsEntry> supplyFtpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(manhattanInboundSettings.getFtpServers().get("supply").getHost());
		factory.setPort(manhattanInboundSettings.getFtpServers().get("supply").getPort());
		factory.setUser(manhattanInboundSettings.getFtpServers().get("supply").getUsername());
		factory.setPassword(manhattanInboundSettings.getFtpServers().get("supply").getPassword());
		factory.setAllowUnknownKeys(true);

		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = MANHATTAN_SUPPLY_SFTP_CHANNEL)
	public MessageHandler supplySftpHandler() {
		SftpMessageHandler handler = new SftpMessageHandler((supplyFtpSessionFactory()));
		handler.setRemoteDirectoryExpression(new LiteralExpression(manhattanInboundSettings.getFtpServers().get("supply").getRemotePath()));
		handler.setUseTemporaryFileName(false);
		handler.setFileNameGenerator(message -> ((File)message.getPayload()).getName());
		return handler;
	}

	@Bean(name = "hmWallaceFtpSessionFactory")
	public SessionFactory<ChannelSftp.LsEntry> hmWallaceFtpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();

		factory.setHost(manhattanInboundSettings.getFtpServers().get("hmwallace").getHost());
		factory.setPort(manhattanInboundSettings.getFtpServers().get("hmwallace").getPort());
		factory.setUser(manhattanInboundSettings.getFtpServers().get("hmwallace").getUsername());
		factory.setPassword(manhattanInboundSettings.getFtpServers().get("hmwallace").getPassword());
		factory.setAllowUnknownKeys(true);

		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = MANHATTAN_HMWALLACE_SFTP_CHANNEL)
	public MessageHandler hmWallaceSftpHandler() {
		SftpMessageHandler handler = new SftpMessageHandler((supplyFtpSessionFactory()));
		handler.setRemoteDirectoryExpression(new LiteralExpression(manhattanInboundSettings.getFtpServers().get("hmwallace").getRemotePath()));
		handler.setUseTemporaryFileName(false);
		handler.setFileNameGenerator(message -> ((File)message.getPayload()).getName());
		return handler;
	}

	@MessagingGateway
	public interface ManhattanOutboundGateway {
		@Gateway(requestChannel = MANHATTAN_SUPPLY_SFTP_CHANNEL)
		void sendManhattanSupplyFileSftp(File file);

		@Gateway(requestChannel = MANHATTAN_HMWALLACE_SFTP_CHANNEL)
		void sendManhattanHmWallaceFileSftp(File file);
	}
}
