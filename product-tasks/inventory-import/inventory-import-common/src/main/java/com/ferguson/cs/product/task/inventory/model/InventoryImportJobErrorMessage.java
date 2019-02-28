package com.ferguson.cs.product.task.inventory.model;

import com.ferguson.cs.utilities.StringMappedEnum;

public enum InventoryImportJobErrorMessage implements StringMappedEnum {
	MISSING_FTP_URL("Missing an FTP URL"),
	MISSING_FTP_FILENAME("Missing an FTP filename"),
	MISSING_FTP_USER("Missing an FTP user"),
	SFTP_FILE_TRANSFER_ERROR("SFTP file transfer failed. Exception: %s"),
	FTP_FILE_TRANSFER_ERROR("FTP file transfer failed, Exception: %s"),
	EMAIL_RETRIEVAL_ERROR("Failed to retrieve email, Exception: %s"),
	UNABLE_TO_CREATE_ATTACHMENT_DIRECTORY("Failed to create email attachment directory"),
	EMAIL_ATTACHMENT_DOWNLOAD_ERROR("Encountered an error while downloading attachment, Exception: %s"),
	EMAIL_ATTACHMENT_UNZIP_ERROR("Encountered an error while attempting to unzip attachment, Exception: %s"),
	EMAIL_ATTACHMENT_DAT_READ_ERROR("Encountered an error while attempting to read DAT file, Exception: %s"),
	EMAIL_ATTACHMENT_UNKNOWN_CONTENT_TYPE("Attachment is of unknown content type"),
	EMAIL_ATTACHMENT_UKNOWN_ERROR("Attempting to download or read attachment resulted in unknown error, Exception: %s")
			;

	private final String stringValue;

	InventoryImportJobErrorMessage(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}
}
