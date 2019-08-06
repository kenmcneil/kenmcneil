package com.ferguson.cs.product.task.wiser;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("360pi")
@Component
public class ThreeSixtyPiSettings {
	private String ftpUsername;
	private String ftpPassword;
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpFolder;

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
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

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}
}
