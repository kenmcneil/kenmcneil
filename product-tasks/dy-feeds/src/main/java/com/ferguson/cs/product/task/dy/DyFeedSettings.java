package com.ferguson.cs.product.task.dy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("dy")
@Component
public class DyFeedSettings {
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpRoot;
	private String ftpUsername;
	private String ftpPrivateKey;
	private String localFilePath;
	private String localFileName;

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	public String getFtpPrivateKey() {
		return ftpPrivateKey;
	}

	public void setFtpPrivateKey(String ftpPrivateKey) {
		this.ftpPrivateKey = ftpPrivateKey;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpRoot() {
		return ftpRoot;
	}

	public void setFtpRoot(String ftpRoot) {
		this.ftpRoot = ftpRoot;
	}
}
