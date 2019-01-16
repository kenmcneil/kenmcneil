package com.ferguson.cs.product.task.inventory.model;

public class FtpInventoryImportJobLog extends InventoryImportJobLog {
	private String fileName;
	private Boolean isSftp;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getSftp() {
		return isSftp;
	}

	public void setSftp(Boolean sftp) {
		isSftp = sftp;
	}
}
