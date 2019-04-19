package com.ferguson.cs.product.task.inventory;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.net.ftp.FTP;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.remote.session.SessionFactoryLocator;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import com.ferguson.cs.product.task.inventory.model.VendorFtpData;

class VendorFtpSessionFactoryLocator implements SessionFactoryLocator {

	private final Map<Object,SessionFactory> sessionFactoryMap = new HashMap();
	private static final int MILLISECOND_TIMEOUT = 15000;

	@Override
	public SessionFactory getSessionFactory(Object key) {
		SessionFactory sessionFactory = sessionFactoryMap.get(key);

		if ( sessionFactory == null){
			sessionFactory = generateSessionFactory((VendorFtpData)key);
			sessionFactoryMap.put(key,sessionFactory);
		}

		return sessionFactory;
	}

	private SessionFactory generateSessionFactory(VendorFtpData key){


		if(key.getFtpPort() == InventoryImportCommonConfiguration.FTP_PORT) {
			DefaultFtpSessionFactory sessionFactory = new DefaultFtpSessionFactory();
			sessionFactory.setHost(key.getFtpUrl());
			sessionFactory.setPort(key.getFtpPort());
			sessionFactory.setUsername(key.getFtpUser());
			sessionFactory.setPassword(key.getFtpPassword());
			sessionFactory.setConnectTimeout(MILLISECOND_TIMEOUT);
			sessionFactory.setDataTimeout(MILLISECOND_TIMEOUT);
			if(key.getFtpFilename().toLowerCase().endsWith(".zip")) {
				sessionFactory.setFileType(FTP.BINARY_FILE_TYPE);
			} else {
				sessionFactory.setFileType(FTP.ASCII_FILE_TYPE);
			}
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