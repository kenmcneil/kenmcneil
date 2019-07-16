package com.ferguson.cs.product.task.image.decider;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;

import com.ferguson.cs.product.task.image.SupplyProductImageFileNameHelper;
import com.ferguson.cs.product.task.image.SupplyProductImageFileNameHelper.SupplyProductImageFileName;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 *
 */
public class DeciderDirectoryHasImportFiles implements JobExecutionDecider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeciderDirectoryHasImportFiles.class);

	private final SessionFactory<ChannelSftp.LsEntry> sessionFactroy;
	private final String ftpDirectory;

	public DeciderDirectoryHasImportFiles(SessionFactory<ChannelSftp.LsEntry> sessionFactroy, String ftpDirectory) {
		this.sessionFactroy = sessionFactroy;
		this.ftpDirectory = ftpDirectory;
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		LOGGER.info("DeciderDirectoryHasImportFiles executing ... ");
		LOGGER.info("Decideing if files exist for processing ...");

		if (this.sessionFactroy == null) {
			throw new IllegalStateException("sessionFactroy is not initialized. Pass on construction.");
		}
		if (this.ftpDirectory == null) {
			throw new IllegalStateException("ftpDirectory is not initialized. Pass on construction.");
		}

		LsEntry[] lsEntries = null;
		try (Session<LsEntry> ftpSession = sessionFactroy.getSession()) {
			try {
				lsEntries = ftpSession.list(this.ftpDirectory);
			} catch (IOException e) {
				throw new RuntimeException(
						String.format("Error getting ftp list for configured directory ... | %s", this.ftpDirectory),
						e);
			}
		}

		LOGGER.info(String.format("ftp directory length (raw): %s", lsEntries.length));

		for (LsEntry lsEntry : lsEntries) {
			final SupplyProductImageFileName supplyProductImageFileName = SupplyProductImageFileNameHelper
					.toSupplyProductImageFileName(lsEntry.getFilename());
			if (supplyProductImageFileName.getHasFileNameErrors()) {
				LOGGER.info(String.format("file name error triggered skip ... %s", lsEntry.getFilename()));
				for (String fileNameError : supplyProductImageFileName.getFileNameErrors()) {
					LOGGER.info(String.format(" - detail ... %s", fileNameError));
				}
			} else {
				LOGGER.info("supply product image files detected for import, proceed ...");
				return new FlowExecutionStatus("true");
			}
		}

		LOGGER.info("No supply product image files to process ...");
		return new FlowExecutionStatus("false");
	}

}
