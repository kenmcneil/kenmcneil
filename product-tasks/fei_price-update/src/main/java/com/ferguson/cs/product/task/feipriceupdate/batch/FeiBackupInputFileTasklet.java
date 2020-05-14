package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.exceprion.FeiPriceUpdateException;

public class FeiBackupInputFileTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiCreateCostUpdateJobTasklet.class);
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;

	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;

	public FeiBackupInputFileTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings) {
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		List<String> inputResources;
		if (executionContext.containsKey(FeiCreatePriceUpdateTempTableTasklet.INPUT_DATA_FILE)) {
			inputResources = (List<String>) executionContext.get(FeiCreatePriceUpdateTempTableTasklet.INPUT_DATA_FILE);

			// Create backup dir if it does not exist
			File backupFolder = new File(feiPriceUpdateSettings.getBackupFolderPath());
			if (!backupFolder.exists()) {
				backupFolder.mkdir();
			}

			for (int idx = 0; idx < inputResources.size(); idx++) {
				String from = feiPriceUpdateSettings.getInputFilePath() + inputResources.get(idx);
				String to = feiPriceUpdateSettings.getBackupFolderPath() + inputResources.get(idx);
				LOGGER.info("FeiBackupInputFileTasklet - Backing up file {} to {}", from, to);
				Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);

			}
		} else {
			throw new FeiPriceUpdateException(
					"FeiBackupInputFileTasklet - Input file resources not defined in ExecutionContext");
		}

		return RepeatStatus.FINISHED;
	}
}
