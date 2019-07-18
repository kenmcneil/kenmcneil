package com.ferguson.cs.product.task.inventory;

public class FileTransferProperties {
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String remotePath;
	private String localPath;
	private String storagePath;
	private Boolean uploadFile;
	private Boolean storeFile;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public Boolean getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(Boolean uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public Boolean getStoreFile() {
		return storeFile;
	}

	public void setStoreFile(Boolean storeFile) {
		this.storeFile = storeFile;
	}
}
