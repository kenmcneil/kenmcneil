package com.ferguson.cs.product.task.feipricefeed;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("fei-price")
@Component
public class FeiPriceSettings {
	private Map<String,String> locations;
	private String temporaryFilePath;
	private String ftpUrl;
	private Integer ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpFolder;
	private List<String> whiteLabelBrands;

	public Map<String, String> getLocations() {
		return locations;
	}

	public void setLocations(Map<String, String> locations) {
		this.locations = locations;
	}

	public String getTemporaryFilePath() {
		return temporaryFilePath;
	}

	public void setTemporaryFilePath(String temporaryFilePath) {
		this.temporaryFilePath = temporaryFilePath;
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

	public List<String> getWhiteLabelBrands() {
		return whiteLabelBrands;
	}

	public void setWhiteLabelBrands(List<String> whiteLabelBrands) {
		this.whiteLabelBrands = whiteLabelBrands;
	}
}
