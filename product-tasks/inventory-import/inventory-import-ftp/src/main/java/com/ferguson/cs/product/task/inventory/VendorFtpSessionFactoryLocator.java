package com.ferguson.cs.product.task.inventory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.remote.session.SessionFactoryLocator;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import com.ferguson.cs.product.task.inventory.model.VendorFtpData;

class VendorFtpSessionFactoryLocator implements SessionFactoryLocator<Object> {

	private final Map<Object, SessionFactory<? extends Object>> sessionFactoryMap = new HashMap<>();
	private static final int MILLISECOND_TIMEOUT = 15000;

	@SuppressWarnings("unchecked")
	@Override
	public SessionFactory<Object> getSessionFactory(Object key) {
		SessionFactory<? extends Object> sessionFactory = sessionFactoryMap.get(key);

		if (sessionFactory == null) {
			sessionFactory = generateSessionFactory((VendorFtpData) key);
			sessionFactoryMap.put(key, sessionFactory);
		}

		return (SessionFactory<Object>)sessionFactory;
	}

	private SessionFactory<? extends Object> generateSessionFactory(VendorFtpData key) {

		if (key.getFtpPort() == InventoryImportCommonConfiguration.FTP_PORT) {
			DefaultFtpSessionFactory sessionFactory = new DefaultFtpSessionFactory();
			sessionFactory.setHost(key.getFtpUrl());
			sessionFactory.setPort(key.getFtpPort());
			sessionFactory.setUsername(key.getFtpUser());
			sessionFactory.setPassword(key.getFtpPassword());
			sessionFactory.setConnectTimeout(MILLISECOND_TIMEOUT);
			sessionFactory.setDataTimeout(MILLISECOND_TIMEOUT);
			if (key.getFtpFilename().toLowerCase().endsWith(".zip")) {
				sessionFactory.setFileType(FTP.BINARY_FILE_TYPE);
			} else {
				sessionFactory.setFileType(FTP.ASCII_FILE_TYPE);
			}
			sessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
			return sessionFactory;
		} else {
			DefaultSftpSessionFactory sessionFactory = new DefaultSftpSessionFactory();
			sessionFactory.setHost(key.getFtpUrl());
			sessionFactory.setPort(key.getFtpPort());
			sessionFactory.setUser(key.getFtpUser());
			sessionFactory.setPassword(key.getFtpPassword());
			sessionFactory.setAllowUnknownKeys(true);
			sessionFactory.setTimeout(MILLISECOND_TIMEOUT);
			return sessionFactory;
		}
	}
}