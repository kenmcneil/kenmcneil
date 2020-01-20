package com.ferguson.cs.product.task.inventory.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.InventoryImportException;
import com.ferguson.cs.product.task.inventory.InventoryImportSettings;
import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobEmailAttachment;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobError;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobErrorMessage;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobStatus;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobType;
import com.ferguson.cs.product.task.inventory.utility.ZipUtil;

import net.freeutils.tnef.TNEF;
import net.freeutils.tnef.TNEFInputStream;

@Service("emailInventoryImportService")
public class EmailInventoryImportServiceImpl implements InventoryImportService {

	private EmailService emailService;
	private InventoryImportSettings inventoryImportSettings;
	private InventoryImportJobLogService inventoryImportJobLogService;

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired
	public void setInventoryImportSettings(InventoryImportSettings inventoryImportSettings) {
		this.inventoryImportSettings = inventoryImportSettings;
	}

	@Autowired
	public void setInventoryImportJobLogService(InventoryImportJobLogService inventoryImportJobLogService) {
		this.inventoryImportJobLogService = inventoryImportJobLogService;
	}

	@Override
	public void importInventory() {
		EmailInventoryImportJobLog emailInventoryImportJobLog = new EmailInventoryImportJobLog();
		emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.IN_PROGRESS);
		emailInventoryImportJobLog.setJobType(InventoryImportJobType.EMAIL);
		inventoryImportJobLogService.saveInventoryImportJobLog(emailInventoryImportJobLog);

		List<Message> messages;
		try {
			messages = emailService.retrieveEmailMessages();
		} catch (MessagingException e) {
			InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
			inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
			inventoryImportJobError.setErrorMessage(StringUtils
					.truncate(String
					.format(InventoryImportJobErrorMessage.EMAIL_RETRIEVAL_ERROR.getStringValue(), e.getCause().toString()),255));
			emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.FAILED);
			inventoryImportJobLogService.saveInventoryImportJobLog(emailInventoryImportJobLog);

			throw new InventoryImportException("Unable to retrieve inventory information from mail", e);
		}

		File attachmentDir = new File(inventoryImportSettings.getInventoryDirectory());
		if (!attachmentDir.exists() && !attachmentDir.mkdirs()) {
			InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
			inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
			inventoryImportJobError.setErrorMessage(InventoryImportJobErrorMessage.UNABLE_TO_CREATE_ATTACHMENT_DIRECTORY
					.getStringValue());
			emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.FAILED);
			inventoryImportJobLogService.saveInventoryImportJobLog(emailInventoryImportJobLog);
			throw new InventoryImportException("Unable to create attachment directory: " + attachmentDir.getAbsolutePath());
		}

		for (Message message : messages) {
			boolean hasError = false;
			try {
				Folder folder = message.getFolder();
				if (!folder.isOpen()) {
					folder.open(Folder.READ_WRITE);
				}
				Object content = message.getDataHandler().getContent();
				if (content instanceof Multipart) {
					Multipart multiPart = (Multipart) message.getContent();
					int numberOfParts = multiPart.getCount();

					for (int partCount = 0; partCount < numberOfParts; partCount++) {
						BodyPart part = multiPart.getBodyPart(partCount);

						if (!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
								&& !StringUtils.isNotBlank(part.getFileName())) {
							continue; // dealing with attachments only
						}

						// Validate Attachment based on extension.
						String fileExtension = FilenameUtils.getExtension(part.getFileName()).toLowerCase();
						if (!fileExtension.equals("csv") && !fileExtension.equals("txt")
								&& !fileExtension.equals("xlsx") && !fileExtension.equals("zip")
								&& !fileExtension.equals("dat")) {
							continue;
						}

						String attachFilePath = attachmentDir.getAbsolutePath().concat("/").concat(part.getFileName());
						InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment = new InventoryImportJobEmailAttachment();
						inventoryImportJobEmailAttachment.setFilename(part.getFileName());
						try (InputStream inputStream = (InputStream)part.getContent();
								FileOutputStream fos = new FileOutputStream(new File(attachFilePath))) {
							IOUtils.copy(inputStream, fos);
						} catch (Exception e) {
							addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_DOWNLOAD_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
							hasError = true;
						}

						try {
							if (part.getFileName().toLowerCase().endsWith(".zip")) {

								ZipUtil.unZip(new File(inventoryImportSettings.getInventoryDirectory() + "/" + part
										.getFileName()), new File(inventoryImportSettings
										.getInventoryDirectory()));
								inventoryImportJobEmailAttachment.setWasSuccessful(true);
								emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);
							}
						} catch (IOException e) {
							addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_UNZIP_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
							hasError = true;
						}

						try {
							if (part.getFileName().toLowerCase().endsWith(".dat")) {
								readDatFile(attachmentDir.getAbsolutePath() + "/" + part.getFileName(),
										attachmentDir.getAbsolutePath());
								inventoryImportJobEmailAttachment.setWasSuccessful(true);
								emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);
							}
						} catch (IOException e) {
							addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_DAT_READ_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
							hasError = true;
						}
						if (!hasError) {
							inventoryImportJobEmailAttachment.setWasSuccessful(true);
							emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);
						}
					}

				} else if (content instanceof InputStream) {
					String contentType = message.getContentType();
					String fileName = contentType.substring(contentType.lastIndexOf('=') + 1, contentType.length());
					InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment = new InventoryImportJobEmailAttachment();
					inventoryImportJobEmailAttachment.setFilename(fileName);

					try (
							InputStream contentInputStream = (InputStream) content;
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							FileOutputStream fos = new FileOutputStream(attachmentDir.getAbsolutePath() + "/" + fileName
									.toLowerCase())
					) {

						int c;

						while ((c = contentInputStream.read()) != -1) {
							out.write(c);
						}
						out.writeTo(fos);
					} catch (IOException e) {
						addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_DOWNLOAD_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
						hasError = true;
					}
					try {
						if (fileName.toLowerCase().endsWith(".dat")) {
							// Read DAT file and extract content
							readDatFile(attachmentDir.getAbsolutePath() + "/" + fileName,
									attachmentDir.getAbsolutePath());
						}
					} catch (IOException e) {
						addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_DAT_READ_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
						hasError = true;
					}
					if (!hasError) {
						inventoryImportJobEmailAttachment.setWasSuccessful(true);
						emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);
					}
				} else {
					InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
					inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
					inventoryImportJobError.setErrorMessage(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_UNKNOWN_CONTENT_TYPE.toString());
					emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
				}
			} catch (Exception e) {
				InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
				inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
				inventoryImportJobError.setErrorMessage(StringUtils.truncate(String.format(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_UKNOWN_ERROR.getStringValue(), e.toString()),255));
				emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			}
		}
		List<InventoryImportJobEmailAttachment> attachmentLogList = emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList();
		boolean hasSuccesses = attachmentLogList.stream().anyMatch(InventoryImportJobEmailAttachment::getWasSuccessful);
		if (hasSuccesses && !emailInventoryImportJobLog.getErrors().isEmpty()) {
			emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.PARTIAL_FAILURE);
		} else if (hasSuccesses || emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().isEmpty()) {
			emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.COMPLETE);
		} else {
			emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.FAILED);
		}
		inventoryImportJobLogService.saveInventoryImportJobLog(emailInventoryImportJobLog);

	}

	private void readDatFile(String datFilePath, String destDirectory) throws IOException {
		File datFile = new File(datFilePath);
		try (TNEFInputStream tnefIn = new TNEFInputStream(datFile)) {
			TNEF.extractContent(tnefIn, destDirectory);
		}
		FileUtils.deleteQuietly(datFile);
	}

	private void addAttachmentError(InventoryImportJobErrorMessage errorMessage, EmailInventoryImportJobLog emailInventoryImportJobLog, InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment, Exception e) {
		InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
		inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
		if (e == null) {
			inventoryImportJobError.setErrorMessage(errorMessage.getStringValue());
		} else {
			inventoryImportJobError.setErrorMessage(StringUtils.truncate(String.format(errorMessage.getStringValue(), e.getCause().toString()), 255));
		}
		inventoryImportJobEmailAttachment.setWasSuccessful(false);
		emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);
		emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
	}
}
