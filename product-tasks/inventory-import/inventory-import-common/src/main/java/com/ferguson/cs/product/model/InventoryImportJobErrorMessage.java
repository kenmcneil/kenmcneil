package com.ferguson.cs.product.model;

public enum InventoryImportJobErrorMessage implements StringMappedEnum {
	MISSING_FTP_URL("Missing an FTP URL"),
	MISSING_FTP_FILENAME("Missing an FTP filename"),
	MISSING_FTP_USER("Missing an FTP user"),
	SFTP_FILE_TRANSFER_ERROR("SFTP file transfer failed. Exception: %s"),
	FTP_FILE_TRANSFER_ERROR("FTP file transfer failed, Exception: %s");

	private final String stringValue;

	InventoryImportJobErrorMessage(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
