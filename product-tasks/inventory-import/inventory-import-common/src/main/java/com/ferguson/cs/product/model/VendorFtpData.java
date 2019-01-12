package com.ferguson.cs.product.model;

import java.io.Serializable;

public class VendorFtpData implements Serializable{
	private static final Long serialVersionUID = 1L;

	private Integer uid;
	private String vendorId;
	private String vendorName;
	private String ftpUrl;
	private String ftpPath;
	private String ftpFilename;
	private String ftpUser;
	private String ftpPassword;
	private Integer ftpPort;

	public VendorFtpData(){}

	public VendorFtpData(VendorFtpData source) {
		this.uid = source.uid;
		this.vendorId = source.vendorId;
		this.vendorName = source.vendorName;
		this.ftpUrl = source.ftpUrl;
		this.ftpPath = source.ftpPath;
		this.ftpFilename = source.ftpFilename;
		this.ftpUser = source.ftpUser;
		this.ftpPassword = source.ftpPassword;
		this.ftpPort = source.ftpPort;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	public String getFtpFilename() {
		return ftpFilename;
	}

	public void setFtpFilename(String ftpFilename) {
		this.ftpFilename = ftpFilename;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(Integer ftpPort) {
		this.ftpPort = ftpPort;
	}
}
