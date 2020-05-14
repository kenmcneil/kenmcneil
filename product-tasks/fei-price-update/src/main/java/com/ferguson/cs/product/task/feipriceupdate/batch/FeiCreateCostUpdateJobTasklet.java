package com.ferguson.cs.product.task.feipriceupdate.batch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.exceprion.FeiPriceUpdateException;
import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceJobStatus;
import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceType;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;
import com.ferguson.cs.utilities.DateUtils;

public class FeiCreateCostUpdateJobTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiCreateCostUpdateJobTasklet.class);

	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;

	@Value("#{stepExecution.jobExecution.executionContext}")
	private ExecutionContext executionContext;

	public FeiCreateCostUpdateJobTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		List<String> inputResources;
		Integer inputFileRecordCount = executionContext.getInt("READ_COUNT", -1);

		if (executionContext.containsKey(FeiCreatePriceUpdateTempTableTasklet.INPUT_DATA_FILE)) {
			inputResources = (List<String>) executionContext.get(FeiCreatePriceUpdateTempTableTasklet.INPUT_DATA_FILE);
		} else {
			throw new FeiPriceUpdateException(
					"CreateCostUpdateJobTasklet - Input file resources no defined in ExecutionContext");
		}

		// Need to create the CostUploaderJob. Need the ID for downstream processing
		CostUpdateJob job = feiPriceUpdateService.createCostUploadJob(inputResources.get(0),
				CostPriceType.PRICEBOOK_CSV, DateUtils.now(), feiPriceUpdateSettings.getCostUpdateJobUserid());

		if (job == null || job.getId() == null) {
			throw new FeiPriceUpdateException("CreateCostUpdateJobTasklet - Error creating Cost Upload Job");
		}

		PriceBookLoadCriteria criteria = new PriceBookLoadCriteria();
		criteria.setJobId(job.getId());
		criteria.setTempTableName(feiPriceUpdateSettings.getTempTableName());
		criteria.setDeleteCost(false);
		
		Integer updateRecordCount = feiPriceUpdateService.loadPriceBookCostUpdatesFromTempTable(criteria);
		LOGGER.info("Creating CostUpdateJob with JobID: {}, inputFile: {}, record count (P1 and P22): {}", job.getId(),
				inputResources.get(0), updateRecordCount);

		// There is going to be twice the number of records as were in the input file
		// since we had to create the P22 records. All the input records are P1
		if (inputFileRecordCount != (updateRecordCount / 2)) {
			LOGGER.warn("FEI Price Update processing: {} failed validation rules",
					inputFileRecordCount - (updateRecordCount / 2));
		}

		// If no records then then no need to execute update
		if (updateRecordCount > 0) {
			job.setStatus(CostPriceJobStatus.ENTERED.getMessageTemplate());
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, -5);
			job.setProcessOn(cal.getTime());
			feiPriceUpdateService.updateCostUploadJob(job);
			LOGGER.info("FEI Price Update -Running PriceBook Cost Updater for job: {}, process on: {}", job.getId(),
					job.getProcessOn());

			// Execute the stored procedure
			feiPriceUpdateService.executePriceBookCostUpdater(job.getId());

			CostUpdateJob completedCostUpdateJob = feiPriceUpdateService.getCostUpdateJob(job.getId());

			LOGGER.info("FEI Cost Update Job ID {} Execution Status : {}", completedCostUpdateJob.getId(),
					completedCostUpdateJob.getStatus());

			// TODO : Send email?
			if (!completedCostUpdateJob.getStatus()
					.equalsIgnoreCase(CostPriceJobStatus.COMPLETE.getMessageTemplate())) {
				LOGGER.error("FEI Cost Update: None COMPLETE status returned from job execution: {}",
						CostPriceJobStatus.COMPLETE.getMessageTemplate());
			}

		} else {
			job.setStatus(CostPriceJobStatus.ERROR_VALIDATION.getMessageTemplate());
			feiPriceUpdateService.updateCostUploadJob(job);
		}

		return RepeatStatus.FINISHED;
	}
}
