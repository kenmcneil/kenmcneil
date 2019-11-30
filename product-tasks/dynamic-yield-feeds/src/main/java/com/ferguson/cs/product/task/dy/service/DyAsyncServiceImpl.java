package com.ferguson.cs.product.task.dy.service;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
@EnableAsync
public class DyAsyncServiceImpl implements DyAsyncService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DyAsyncServiceImpl.class);

	@Async
	@Override
	public Future<Boolean> sendSftpFile(Integer siteId, DyFeedSettings dyFeedSettings, Map<Integer, Resource> resources) throws JSchException, SftpException {
		ChannelSftp channelSftp = setupJsch(siteId, dyFeedSettings);
		channelSftp.connect();

		try {
			channelSftp.cd(dyFeedSettings.getFtpRoot()
					+ dyFeedSettings.getSiteUsername().get(siteId));
			channelSftp.put(((FileSystemResource) resources.get(siteId)).getPath(), ".");
		} catch (SftpException e) {
			LOGGER.debug("Base directory not found. Creating.");

			channelSftp.mkdir(dyFeedSettings.getFtpRoot()
					+ dyFeedSettings.getSiteUsername().get(siteId));

			channelSftp.cd(dyFeedSettings.getFtpRoot()
					+ dyFeedSettings.getSiteUsername().get(siteId));
			channelSftp.put(((FileSystemResource) resources.get(siteId)).getPath(), ".");
		}
		try {
			channelSftp.rm("./" + dyFeedSettings.getTempFilePrefix() + dyFeedSettings.getTempFileSuffix());
		} catch (SftpException e) {
			LOGGER.debug("File not found for removal. Skipping.");
		}

		channelSftp.rename("./" + ((FileSystemResource) resources.get(siteId)).getFilename(), "./" + dyFeedSettings.getTempFilePrefix() + dyFeedSettings.getTempFileSuffix());
		channelSftp.exit();

		return new AsyncResult<>(Boolean.TRUE);
	}

	/**
	 * Create FTP connection based on site username
	 *
	 * @param siteId
	 * @param dyFeedSettings
	 * @return an open ChannelSftp
	 */
	private ChannelSftp setupJsch(Integer siteId, DyFeedSettings dyFeedSettings) throws JSchException {
		JSch jsch = new JSch();
		jsch.addIdentity("build.com", dyFeedSettings.getFtpPrivateKey().getBytes(), null, null);
		Session jschSession = jsch.getSession(dyFeedSettings.getSiteUsername().get(siteId), dyFeedSettings.getFtpUrl());
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		jschSession.setConfig(config);
		jschSession.connect();
		return (ChannelSftp) jschSession.openChannel("sftp");
	}
}
