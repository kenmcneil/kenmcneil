package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.notification.NotificationService;
import com.ferguson.cs.product.task.feipriceupdate.notification.SlackMessageType;

public class FeiCreatePriceUpdateTempTableTasklet implements Tasklet {

	public static final String INPUT_DATA_FILES = "inputFileName";
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

		// Make sure we have a 2 data files. 1 PB1 and 1 PB22. If not, decider will check this and end job
		Resource[] resources;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		resources = patternResolver.getResources("file:" + feiPriceUpdateSettings.getInputFilePath() + "*.csv");

		if (resources != null && resources.length > 0) {

			List<String> fileNames = validateInputResources(resources);

			// Should have 2 files.  A PB1 and a PB22
			if (fileNames != null && fileNames.size() == 2) {
				chunkContext.getStepContext().getStepExecution().getJobExecution().
				getExecutionContext().put(INPUT_DATA_FILES, fileNames);

				// Add a reference to the individual file and which one it is.
				if (fileNames.get(0).toUpperCase().startsWith(feiPriceUpdateSettings.getPb1InputFilePrefix().toUpperCase())) {
					chunkContext.getStepContext().getStepExecution().getJobExecution().
					getExecutionContext().put(PB1_INPUT_FILE, fileNames.get(0));
					chunkContext.getStepContext().getStepExecution().getJobExecution().
					getExecutionContext().put(PB22_INPUT_FILE, fileNames.get(1));
				} else {
					chunkContext.getStepContext().getStepExecution().getJobExecution().
					getExecutionContext().put(PB1_INPUT_FILE, fileNames.get(1));
					chunkContext.getStepContext().getStepExecution().getJobExecution().
					getExecutionContext().put(PB22_INPUT_FILE, fileNames.get(0));
				}

				// Making sure temp table does not exist first by making a call to drop it.
				feiPriceUpdateService.dropTempTable(feiPriceUpdateSettings.getTempTableName());
				feiPriceUpdateService.createTempTable(feiPriceUpdateSettings.getTempTableName());
			} else {
				// Notify the slack channel that there was no input file
				notificationService.message("FEI Price Update DataFlow task:"
						+ chunkContext.getStepContext().getJobName()
						+ " input file discrepancy.", SlackMessageType.WARNING);
			}
		} else {
			// Notify the slack channel that there was no input file
			notificationService.message("FEI Price Update DataFlow task:"
					+ chunkContext.getStepContext().getJobName()
					+ " found no input file for processing.", SlackMessageType.WARNING);
		}

		return RepeatStatus.FINISHED;
	}

	// loop over our resource files and check for the specific pb1 and pb22 files ignoring case.
	// We only want 1 PB1 and 1 PB22.  If there is a file that does not match given input file
	// prefix for pb1 or pb22 it will be ignored.
	private List<String> validateInputResources(Resource[] resources) {

		int pb1Index = -1;
		int pb22Index = -1;
		List<String> inputFiles = new ArrayList<String>();

		for (int i = 0 ; i < resources.length; i++) {
			// Check for PB1 file
			if (resources[i].getFilename().toUpperCase().startsWith(feiPriceUpdateSettings.getPb1InputFilePrefix().toUpperCase())) {
				// If pb1 index is not -1 we have multiple pb1 input files - That's a problem.
				if (pb1Index != -1) {
					break;
				}
				pb1Index = i;
			} else 	if (resources[i].getFilename().toUpperCase().startsWith(feiPriceUpdateSettings.getPb22InputFilePrefix().toUpperCase())) {
				// If pb22 index is not -1 we have multiple pb22 input files - That's a problem.
				if (pb22Index != -1) {
					break;
				}
				pb22Index = i;
			}
		}

		if (pb1Index >= 0 && pb22Index >= 0) {
			inputFiles.add(resources[pb1Index].getFilename());
			inputFiles.add(resources[pb22Index].getFilename());
		}

		return inputFiles;
	}
}