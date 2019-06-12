package com.ferguson.cs.product.task.image;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SupplyImageImportFtpConfiguration {

	private Boolean ftpEnabled;
	private String ftpHost;
	private Integer ftpPort;
	private String userId;
	private String password;
	private String baseFilePath;
	private Integer maxFilesToSync;
	private String errorFilePath;
	

	public Boolean getFtpEnabled() {
		return ftpEnabled;
	}

	public void setFtpEnabled(Boolean ftpEnabled) {
		this.ftpEnabled = ftpEnabled;
	}

	public String getFtpHost() {
		return ftpHost;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBaseFilePath() {
		return baseFilePath;
	}

	public void setBaseFilePath(String baseFilePath) {
		this.baseFilePath = baseFilePath;
	}

	public Integer getMaxFilesToSync() {
		return maxFilesToSync;
	}

	public void setMaxFilesToSync(Integer maxFilesToSync) {
		this.maxFilesToSync = maxFilesToSync;
	}

	
	public String getErrorFilePath() {
		return errorFilePath;
	}

	public void setErrorFilePath(String errorFilePath) {
		this.errorFilePath = errorFilePath;
	}

	@Override
	public String toString() {
		String[] exclued = { "privateKey", "password" };
		return ReflectionToStringBuilder.toStringExclude(this, exclued);
	}

}
