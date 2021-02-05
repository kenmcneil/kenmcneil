package com.ferguson.cs.product.task.inventory.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.EmailInventoryImportSettings;
import com.ferguson.cs.product.task.inventory.InventoryImportException;
import com.ferguson.cs.product.task.inventory.InventoryImportSettings;
import com.ferguson.cs.product.task.inventory.model.EmailInventoryImportJobLog;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobEmailAttachment;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobError;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobErrorMessage;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobStatus;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobType;
import com.ferguson.cs.product.task.inventory.utility.ZipUtil;
import com.microsoft.graph.models.extensions.Attachment;
import com.microsoft.graph.models.extensions.IGraphServiceClient;

import net.freeutils.tnef.TNEF;
import net.freeutils.tnef.TNEFInputStream;

@Service("emailInventoryImportService")
public class EmailInventoryImportServiceImpl implements InventoryImportService {
	private static final Logger LOG = LoggerFactory.getLogger(EmailInventoryImportServiceImpl.class);

	private IGraphServiceClient graphServiceClient;
	private InventoryImportSettings inventoryImportSettings;
	private EmailInventoryImportSettings emailInventoryImportSettings;
	private InventoryImportJobLogService inventoryImportJobLogService;

	@Autowired
	public void setInventoryImportSettings(InventoryImportSettings inventoryImportSettings) {
		this.inventoryImportSettings = inventoryImportSettings;
	}

	@Autowired
	public void setInventoryImportJobLogService(InventoryImportJobLogService inventoryImportJobLogService) {
		this.inventoryImportJobLogService = inventoryImportJobLogService;
	}

	@Autowired
	public void setGraphServiceClient(IGraphServiceClient graphServiceClient) {
		this.graphServiceClient = graphServiceClient;
	}

	@Autowired
	public void setEmailInventoryImportSettings(EmailInventoryImportSettings emailInventoryImportSettings) {
		this.emailInventoryImportSettings = emailInventoryImportSettings;
	}

	@Override
	public void importInventory() {
		EmailInventoryImportJobLog emailInventoryImportJobLog = new EmailInventoryImportJobLog();
		emailInventoryImportJobLog.setStatus(InventoryImportJobStatus.IN_PROGRESS);
		emailInventoryImportJobLog.setJobType(InventoryImportJobType.EMAIL);
		inventoryImportJobLogService.saveInventoryImportJobLog(emailInventoryImportJobLog);

		List<String> messageIds;
		try {
			//Get ids of messages for signed in user, filtering to those with attachments
			messageIds = graphServiceClient.me().messages().buildRequest().filter("hasAttachments eq true").select("id")
					.get().getCurrentPage().stream().map(m -> m.id).collect(Collectors.toList());


		} catch (Exception e) {
			InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
			inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
			inventoryImportJobError.setErrorMessage(StringUtils
					.truncate(String
							.format(InventoryImportJobErrorMessage.EMAIL_RETRIEVAL_ERROR.getStringValue(), e.getCause()
									.toString()), 255));
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
			throw new InventoryImportException("Unable to create attachment directory: " + attachmentDir
					.getAbsolutePath());
		}

		for (String messageId : messageIds) {
			boolean hasError = false;
			try {
				//Get the attachments associated to message id
				List<Attachment> attachments = graphServiceClient.me().messages(messageId).attachments().buildRequest()
						.get().getCurrentPage();

				for (Attachment attachment : attachments) {
					//Unfortunately, file type attachments returned from Microsoft's Graph SDK are NOT of type
					//FileAttachment. So we check this field in lieu of a "typeof" check
					if (attachment.oDataType.contains("file")) {

						String attachFilePath = attachmentDir.getAbsolutePath().concat("/").concat(attachment.name);

						String fileExtension = FilenameUtils.getExtension(attachment.name).toLowerCase();
						if (!fileExtension.equals("csv") && !fileExtension.equals("txt")
								&& !fileExtension.equals("xlsx") && !fileExtension.equals("zip")
								&& !fileExtension.equals("dat")) {
							LOG.warn("Skip Attachment (" + attachment.name+ "): no extention match");
							continue;
						}
						InventoryImportJobEmailAttachment inventoryImportJobEmailAttachment = new InventoryImportJobEmailAttachment();
						inventoryImportJobEmailAttachment.setFilename(attachment.name);
						//Content is base 64 encoded, need to decode it
						try (InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder()
								.decode(attachment.getRawObject().get("contentBytes").getAsString().getBytes()));
							 FileOutputStream fos = new FileOutputStream(new File(attachFilePath))) {
							IOUtils.copy(inputStream, fos);
						} catch (Exception e) {
							addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_DOWNLOAD_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
							hasError = true;
						}

						try {
							if (attachment.name.toLowerCase().endsWith(".zip")) {

								ZipUtil.unZip(new File(inventoryImportSettings
										.getInventoryDirectory() + "/" + attachment.name), new File(inventoryImportSettings
										.getInventoryDirectory()));
								inventoryImportJobEmailAttachment.setWasSuccessful(true);
								emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList()
										.add(inventoryImportJobEmailAttachment);
							}
						} catch (IOException e) {
							addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_UNZIP_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
							hasError = true;
						}

						try {
							if (attachment.name.toLowerCase().endsWith(".dat")) {
								readDatFile(attachmentDir.getAbsolutePath() + "/" + attachment.name,
										attachmentDir.getAbsolutePath());
								inventoryImportJobEmailAttachment.setWasSuccessful(true);
								emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList()
										.add(inventoryImportJobEmailAttachment);
							}
						} catch (IOException e) {
							addAttachmentError(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_DAT_READ_ERROR, emailInventoryImportJobLog, inventoryImportJobEmailAttachment, e);
							hasError = true;
						}
						if (!hasError) {
							inventoryImportJobEmailAttachment.setWasSuccessful(true);
							emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList()
									.add(inventoryImportJobEmailAttachment);
						}
					}
				}
				if(emailInventoryImportSettings != null && Boolean.FALSE.equals(emailInventoryImportSettings.getSafeMode())) {
					//Delete message from inbox, unless in safe mode
					graphServiceClient.me().messages(messageId).buildRequest().delete();
				}

			} catch (Exception e) {
				InventoryImportJobError inventoryImportJobError = new InventoryImportJobError();
				inventoryImportJobError.setInventoryImportJobLogId(emailInventoryImportJobLog.getId());
				inventoryImportJobError.setErrorMessage(StringUtils.truncate(String
						.format(InventoryImportJobErrorMessage.EMAIL_ATTACHMENT_UKNOWN_ERROR.getStringValue(), e
								.toString()), 255));
				emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
			}
		}
		List<InventoryImportJobEmailAttachment> attachmentLogList = emailInventoryImportJobLog
				.getInventoryImportJobEmailAttachmentList();
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
			inventoryImportJobError.setErrorMessage(StringUtils
					.truncate(String.format(errorMessage.getStringValue(), e.getCause().toString()), 255));
		}
		inventoryImportJobEmailAttachment.setWasSuccessful(false);
		emailInventoryImportJobLog.getInventoryImportJobEmailAttachmentList().add(inventoryImportJobEmailAttachment);
		emailInventoryImportJobLog.getErrors().add(inventoryImportJobError);
	}
}
