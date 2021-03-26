package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.notification.NotificationService;
import com.ferguson.cs.product.task.feipriceupdate.notification.SlackMessageType;

public class FeiCreatePriceUpdateTempTableTasklet implements Tasklet {

	public static final String PB1_INPUT_FILE = "PB1_inputFile";
	public static final String PB22_INPUT_FILE = "PB22_inputFile";

	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;
	private final NotificationService notificationService;

	public FeiCreatePriceUpdateTempTableTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService,
			NotificationService notificationService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.notificationService = notificationService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// See what input files we have.  We can only have one PB1 and/or one PB22 file.
		List<String> inputFiles = getInputResources(chunkContext);

		if (!CollectionUtils.isEmpty(inputFiles)) {
			// Making sure temp table does not exist first by making a call to drop it.
			feiPriceUpdateService.dropTempTable(feiPriceUpdateSettings.getTempTableName());
			feiPriceUpdateService.createTempTable(feiPriceUpdateSettings.getTempTableName());

			for (String fileName : inputFiles) {

				// Add a reference to the individual file and which one it is.
				if (fileName.toUpperCase().startsWith(feiPriceUpdateSettings.getPb1InputFilePrefix().toUpperCase())) {
					chunkContext.getStepContext().getStepExecution().getJobExecution().
					getExecutionContext().put(PB1_INPUT_FILE, fileName);
				} else if (fileName.toUpperCase().startsWith(feiPriceUpdateSettings.getPb22InputFilePrefix().toUpperCase())) {
					chunkContext.getStepContext().getStepExecution().getJobExecution().
					getExecutionContext().put(PB22_INPUT_FILE, fileName);
				}

			}
		}

		return RepeatStatus.FINISHED;
	}

	// loop over our resource files and check for the specific pb1 and pb22 files ignoring case.
	// Acceptable combinations are: only one PB1 file or only 1 PB22 file or one of each.
	private List<String> getInputResources(ChunkContext chunkContext) throws IOException {

		Resource[] resources;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		resources = patternResolver.getResources("file:" + feiPriceUpdateSettings.getInputFilePath() + "*.csv");
		List<String> inputFiles = new ArrayList<>();
		int pb1Index = -1;
		int pb22Index = -1;
		boolean multipleFiles = false;

		if (resources != null && resources.length > 0) {
			for (int i = 0 ; i < resources.length; i++) {
				// Check for PB1 file
				if (resources[i].getFilename() != null &&
						resources[i].getFilename().toUpperCase().startsWith(feiPriceUpdateSettings.getPb1InputFilePrefix().toUpperCase())) {
					// If pb1 index is not -1 we have multiple pb1 input files - That's a problem.
					if (pb1Index != -1) {
						multipleFiles = true;
						break;
					}
					pb1Index = i;
				} else 	if (resources[i].getFilename() != null &&
						resources[i].getFilename().toUpperCase().startsWith(feiPriceUpdateSettings.getPb22InputFilePrefix().toUpperCase())) {
					// If pb22 index is not -1 we have multiple pb22 input files - That's a problem.
					if (pb22Index != -1) {
						multipleFiles = true;
						break;
					}
					pb22Index = i;
				}
			}

			// If we have multiple PB1 or PB22 files notify slack channel and break
			if (multipleFiles) {
				notificationService.message("FEI Price Update DataFlow task:"
						+ chunkContext.getStepContext().getJobName()
						+ " multiple PB1 or PB22 input files found in input folder.  Unable to continue", SlackMessageType.WARNING);
			} else {
				if (pb1Index >= 0) {
					inputFiles.add(resources[pb1Index].getFilename());
				}
				if (pb22Index >= 0) {
					inputFiles.add(resources[pb22Index].getFilename());
				}
			}
		}

		return inputFiles;
	}
}