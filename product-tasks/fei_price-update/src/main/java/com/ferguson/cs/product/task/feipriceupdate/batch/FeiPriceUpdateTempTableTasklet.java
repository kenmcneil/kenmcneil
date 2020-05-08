package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;

public class FeiPriceUpdateTempTableTasklet implements Tasklet {
	
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;
	
	public FeiPriceUpdateTempTableTasklet(
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// Making sure temp table does not exist first by making a call to drop it if it exists.
		// Then create it
		feiPriceUpdateService.dropTempTable(feiPriceUpdateSettings.getTempTableName());
		feiPriceUpdateService.createTempTable(feiPriceUpdateSettings.getTempTableName());
		return RepeatStatus.FINISHED;
	}

}
