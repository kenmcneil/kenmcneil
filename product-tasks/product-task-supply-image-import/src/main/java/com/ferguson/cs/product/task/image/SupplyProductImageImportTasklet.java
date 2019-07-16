package com.ferguson.cs.product.task.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;

import com.ferguson.cs.product.task.image.SupplyProductImageFileNameHelper.SupplyProductImageFileName;
import com.ferguson.cs.product.task.image.integration.webservices.ImageFileResource;
import com.ferguson.cs.product.task.image.integration.webservices.WebservicesClient;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * This tasklet is used to upload the supply product images from ftp server to
 * Cloudinary and update database.
 * 
 * @author c-chandra
 */
public class SupplyProductImageImportTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupplyProductImageImportTasklet.class);

	private final WebservicesClient wsClient;
	private final SessionFactory<ChannelSftp.LsEntry> sessionFactroy;
	private final String ftpDirectory;
	private Integer numberOfFilesToProcess = 10;
	private final String errorFtpDirectory;

	public SupplyProductImageImportTasklet(SessionFactory<ChannelSftp.LsEntry> sessionFactroy,
			SupplyImageImportFtpConfiguration supplyImageImportFtpConfiguration, WebservicesClient wsClient) {
		this.sessionFactroy = sessionFactroy;
		this.ftpDirectory = supplyImageImportFtpConfiguration.getBaseFilePath();
		this.wsClient = wsClient;
		this.errorFtpDirectory = this.ftpDirectory + "/" + supplyImageImportFtpConfiguration.getErrorFilePath();
		if (supplyImageImportFtpConfiguration.getMaxFilesToSync() != null) {
			numberOfFilesToProcess = supplyImageImportFtpConfiguration.getMaxFilesToSync();
		}
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// Before proceeding check connection to webservices
		if (!this.wsClient.serverUp()) {
			throw new RuntimeException(String.format("Webservices can not be communicated with. Processing stoped. %s",
					this.wsClient.getClientConfiguration() != null
							? this.wsClient.getClientConfiguration().getRemoteServerAddress()
							: "remote server not configured"));
		}

		final UUID uploadUUID = UUID.randomUUID();
		LOGGER.info(String.format("FileRenamingTasklet executing ... %s", uploadUUID));

		final List<SupplyProductImageFileName> filesToProcess = new ArrayList<>();
		LsEntry[] lsEntries = null;
		try (Session<LsEntry> ftpSession = sessionFactroy.getSession()) {
			try {
				lsEntries = ftpSession.list(this.ftpDirectory);
			} catch (IOException e) {
				throw new RuntimeException(
						String.format("Error getting ftp list for configured directory ... | %s", this.ftpDirectory),
						e);
			}

			for (LsEntry file : lsEntries) {

				final SupplyProductImageFileName supplyProductImageFileName = SupplyProductImageFileNameHelper
						.toSupplyProductImageFileName(file.getFilename(), uploadUUID.toString());

				if (!supplyProductImageFileName.getHasFileNameErrors()) {

					LOGGER.info(String.format("Renaming file ... %s", supplyProductImageFileName.getName()));

					// rename
					try {

						ftpSession.rename(this.ftpDirectory + "/" + supplyProductImageFileName.getNameWithExtension(),
								this.ftpDirectory + "/" + supplyProductImageFileName.getUploadFileName());

					} catch (IOException e) {
						throw new RuntimeException(
								String.format("Error renaming file for upload processing ... | %s", this.ftpDirectory),
								e);
					}

					filesToProcess.add(supplyProductImageFileName);

				} else {
					LOGGER.info(String.format("Skipping file ... %s", supplyProductImageFileName.getName()));
					for (String fileNameError : supplyProductImageFileName.getFileNameErrors()) {
						LOGGER.info(" -- " + fileNameError);
					}
				}

				if (filesToProcess.size() >= numberOfFilesToProcess) {
					LOGGER.info(String.format("Number of files to process reached  ... %s", numberOfFilesToProcess));
					break;
				}

			}

		}

		LOGGER.info(String.format("Webservices server up ... %s", this.wsClient.serverUp()));

		try (Session<LsEntry> ftpSession = sessionFactroy.getSession()) {
			for (SupplyProductImageFileName supplyProductImageFileName : filesToProcess) {
				LOGGER.info("Uploading supply product image file ... {} ",
						supplyProductImageFileName.getUploadFileName());

				InputStream inputStream = null;
				try {
					inputStream = ftpSession
							.readRaw(this.ftpDirectory + "/" + supplyProductImageFileName.getUploadFileName());
				} catch (Exception e) {
					// move to an error directory
					moveFileToErrorDir(ftpSession, supplyProductImageFileName.getUploadFileName());
					LOGGER.error("Exception occured while reading file from ftp location: {} ",
							supplyProductImageFileName.getNameWithExtension());
					continue;
				}

				//
				ImageFileResource imageFileResource = null;
				try {
					imageFileResource = new ImageFileResource(IOUtils.toByteArray(inputStream),
							supplyProductImageFileName.getNameWithExtension());
				} catch (Exception e) {
					// move to an error directory
					moveFileToErrorDir(ftpSession, supplyProductImageFileName.getUploadFileName());
					LOGGER.error("Exception occured while managing file inputstream for upload.: {} ",
							supplyProductImageFileName.getNameWithExtension());
					continue;

				}

				try {
					this.wsClient.uploadSupplyProductImageIOStream(supplyProductImageFileName.getNameWithExtension(),
							imageFileResource);
				} catch (Exception e) {
					LOGGER.error("Exception occured for uploading supply product image {}: {} ",
							supplyProductImageFileName.getNameWithExtension(), e.getMessage());
					// move to an error directory
					moveFileToErrorDir(ftpSession, supplyProductImageFileName.getUploadFileName());
					LOGGER.error("Exception occured while uploading supply image file.: {} ",
							supplyProductImageFileName.getNameWithExtension());
					continue;

				}

				try {
					LOGGER.info("Deleting supply product image file from ftp origin ... {} ",
							supplyProductImageFileName.getUploadFileName());
					ftpSession.remove(this.ftpDirectory + "/" + supplyProductImageFileName.getUploadFileName());
				} catch (Exception e) {
					// Log but take no action. The file will be stale for any future runs and will
					// need to be removed manually.
					LOGGER.error(
							"Exception occured while cleaning up import file, file will be left in place for manual removal: {}: {} ",
							supplyProductImageFileName.getNameWithExtension(), e.getMessage());
					continue;

				}

			}

		}

		return RepeatStatus.FINISHED;

	}

	private void moveFileToErrorDir(Session<LsEntry> ftpSession, String fileName) {
		try {
			// Check is error dir exists - if not create in under ftpDirectory
			if (!ftpSession.exists(this.errorFtpDirectory)) {
				ftpSession.mkdir(this.errorFtpDirectory);
			}
			ftpSession.rename(this.ftpDirectory + "/" + fileName, this.errorFtpDirectory + "/" + fileName);
		} catch (IOException e) {
			throw new RuntimeException(
					String.format("Error moving image file %s to error dir %s ", fileName, this.errorFtpDirectory), e);
		}
	}

}
