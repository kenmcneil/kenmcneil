package com.ferguson.cs.product.task.dy.service;

import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.core.io.Resource;

import com.ferguson.cs.product.task.dy.DyFeedSettings;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


public interface DyAsyncService {
	/**
	 * Create site specific JSch FTP connections to send product site files asynchronously
	 *
	 * @param siteId
	 * @param dyFeedSettings
	 * @param resources
	 * @return Future<Boolean>
	 */
	public Future<Boolean> sendSftpFile(Integer siteId, DyFeedSettings dyFeedSettings, Map<Integer, Resource> resources) throws JSchException, SftpException;
}
