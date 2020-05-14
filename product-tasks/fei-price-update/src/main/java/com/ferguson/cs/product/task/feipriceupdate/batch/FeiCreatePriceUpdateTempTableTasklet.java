package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;

public class FeiCreatePriceUpdateTempTableTasklet implements Tasklet {

	public static final String INPUT_DATA_FILE = "inputFileName";

	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;

	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;

	public FeiCreatePriceUpdateTempTableTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// Make sure we have a data file. If not, decider will check this and end job if
		// it does not exist
		// in executionContext. Multiple files are supported.
		Resource[] resources;
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		resources = patternResolver.getResources("file:" + feiPriceUpdateSettings.getInputFilePath() + "*.csv");

		if (resources != null && resources.length > 0) {
			List<String> fileNames = new ArrayList<String>();
			Arrays.stream(resources).forEach(r -> fileNames.add(r.getFilename()));
			this.executionContext.put(INPUT_DATA_FILE, fileNames);

			// Making sure temp table does not exist first by making a call to drop it.
			feiPriceUpdateService.dropTempTable(feiPriceUpdateSettings.getTempTableName());
			feiPriceUpdateService.createTempTable(feiPriceUpdateSettings.getTempTableName());
		}

		return RepeatStatus.FINISHED;
	}

}