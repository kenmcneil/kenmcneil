package com.ferguson.cs.product.task.dy.service;

import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


public interface DyService {
	/**
	 * Create site specific JSch FTP connections to send product site files
	 *  @param siteId
	 * @param dyFeedSettings
	 * @param filename
	 *
	 */
	public void sendSftpFile(Integer siteId, DyFeedSettings dyFeedSettings, String filename) throws JSchException, SftpException;
}
