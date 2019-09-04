package com.ferguson.cs.product.task.wiser;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("wiser")
@Component
public class WiserFeedSettings {
	private String temporaryLocalFilePath;
	private String dateTimeFormat;
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpFolder;
	private String ftpOutputFolder;
	private String fileDownloadLocation;

	public String getTemporaryLocalFilePath() {
		return temporaryLocalFilePath;
	}

	public void setTemporaryLocalFilePath(String temporaryLocalFilePath) {
		this.temporaryLocalFilePath = temporaryLocalFilePath;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
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

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public String getFtpOutputFolder() {
		return ftpOutputFolder;
	}

	public void setFtpOutputFolder(String ftpOutputFolder) {
		this.ftpOutputFolder = ftpOutputFolder;
	}

	public String getFileDownloadLocation() {
		return fileDownloadLocation;
	}

	public void setFileDownloadLocation(String fileDownloadLocation) {
		this.fileDownloadLocation = fileDownloadLocation;
	}
}
