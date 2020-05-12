package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceType;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.utilities.DateUtils;

public class CreateCostUpdateJobTasklet implements Tasklet {
	
	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;
	
	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;
	
	public CreateCostUpdateJobTasklet(
			FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		// Need to create the CostUploaderJob.  Need the ID for downstream processing
		CostUpdateJob job = feiPriceUpdateService.createCostUploadJob("test.csv", CostPriceType.VENDOR_COST_CSV, DateUtils.now(), -1234);
		this.executionContext.put("JobId", job.getId());
		
		System.out.println("*******  JobId : " + job.getId());

		return RepeatStatus.FINISHED;
	}

}
