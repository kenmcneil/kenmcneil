package com.ferguson.cs.product.task.dy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("dy")
@Component
public class DyFeedSettings {
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpRoot;
	private String ftpPrivateKey;
	private String tempFilePrefix = "productfeed";
	private String tempFileSuffix = ".csv";
	private Map<Integer, String> siteUsername = new HashMap<>();

	public String getTempFileSuffix() {
		return tempFileSuffix;
	}

	public void setTempFileSuffix(String tempFileSuffix) {
		this.tempFileSuffix = tempFileSuffix;
	}

	public String getTempFilePrefix() {
		return tempFilePrefix;
	}

	public void setTempFilePrefix(String tempFilePrefix) {
		this.tempFilePrefix = tempFilePrefix;
	}

	public String getFtpPrivateKey() {
		return ftpPrivateKey;
	}

	public void setFtpPrivateKey(String ftpPrivateKey) {
		this.ftpPrivateKey = ftpPrivateKey;
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

	public String getFtpRoot() {
		return ftpRoot;
	}

	public void setFtpRoot(String ftpRoot) {
		this.ftpRoot = ftpRoot;
	}

	public Map<Integer, String> getSiteUsername() {
		return siteUsername;
	}

	public void setSiteUsername(Map<Integer, String> siteUsername) {
		this.siteUsername = siteUsername;
	}
}
