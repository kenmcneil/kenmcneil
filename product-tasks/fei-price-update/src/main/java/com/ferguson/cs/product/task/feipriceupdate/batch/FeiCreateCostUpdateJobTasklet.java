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
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.feipriceupdate.FeiPriceUpdateSettings;
import com.ferguson.cs.product.task.feipriceupdate.data.FeiPriceUpdateService;
import com.ferguson.cs.product.task.feipriceupdate.exceprion.FeiPriceUpdateException;
import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceJobStatus;
import com.ferguson.cs.product.task.feipriceupdate.model.CostPriceType;
import com.ferguson.cs.product.task.feipriceupdate.model.CostUpdateJob;
import com.ferguson.cs.product.task.feipriceupdate.model.PriceBookLoadCriteria;
import com.ferguson.cs.product.task.feipriceupdate.notification.NotificationService;
import com.ferguson.cs.product.task.feipriceupdate.notification.SlackMessageType;
import com.ferguson.cs.utilities.DateUtils;

public class FeiCreateCostUpdateJobTasklet implements Tasklet {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeiCreateCostUpdateJobTasklet.class);

	private final FeiPriceUpdateSettings feiPriceUpdateSettings;
	private final FeiPriceUpdateService feiPriceUpdateService;
	private final NotificationService notificationService;

	public FeiCreateCostUpdateJobTasklet(FeiPriceUpdateSettings feiPriceUpdateSettings,
			FeiPriceUpdateService feiPriceUpdateService,
			NotificationService notificationService) {
		this.feiPriceUpdateService = feiPriceUpdateService;
		this.feiPriceUpdateSettings = feiPriceUpdateSettings;
		this.notificationService = notificationService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		List<String> inputResources = new ArrayList<>();
		ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		Integer Pb1inputFileRecordCount = executionContext.getInt("PB1_READ_COUNT", 0);
		Integer Pb22inputFileRecordCount = executionContext.getInt("PB22_READ_COUNT", 0);
		Integer inputRecordCount = Pb1inputFileRecordCount + Pb22inputFileRecordCount;
		String inputFiles;

		if (executionContext.containsKey(FeiCreatePriceUpdateTempTableTasklet.PB1_INPUT_FILE)) {
			inputResources.add((String)executionContext.get(FeiCreatePriceUpdateTempTableTasklet.PB1_INPUT_FILE));
		}

		if (executionContext.containsKey(FeiCreatePriceUpdateTempTableTasklet.PB22_INPUT_FILE)) {
			inputResources.add((String)executionContext.get(FeiCreatePriceUpdateTempTableTasklet.PB22_INPUT_FILE));
		}

		if (CollectionUtils.isEmpty(inputResources)) {
			throw new FeiPriceUpdateException(
					"CreateCostUpdateJobTasklet - Input file resources not defined in ExecutionContext");
		}

		// Can only have 2 input files max.  If we have 2 then concat them.  createcostUploadJob() wants a filename passed
		if (inputResources.size() == 2) {
			inputFiles = inputResources.get(0) + "-" + inputResources.get(1);
		} else {
			inputFiles = inputResources.get(0);
		}

		// Need to create the CostUploaderJob. Need the ID for downstream processing
		CostUpdateJob job = feiPriceUpdateService.createCostUploadJob(inputFiles,
				CostPriceType.PRICEBOOK_CSV, DateUtils.now(), feiPriceUpdateSettings.getCostUpdateJobUserid());

		if (job == null || job.getId() == null) {
			throw new FeiPriceUpdateException("CreateCostUpdateJobTasklet - Error creating Cost Upload Job");
		}

		PriceBookLoadCriteria criteria = new PriceBookLoadCriteria();
		criteria.setJobId(job.getId());
		criteria.setTempTableName(feiPriceUpdateSettings.getTempTableName());
		criteria.setDeleteCost(false);

		Integer updateRecordCount = feiPriceUpdateService.loadPriceBookCostUpdatesFromTempTable(criteria);
		LOGGER.info("Creating CostUpdateJob with JobID: {}, input files: {}, record count (P1 and P22): {}", job.getId(),
				inputFiles, updateRecordCount);


		if (inputRecordCount != updateRecordCount) {
			LOGGER.warn("FEI Price Update processing.  Input record count: {} was not equal to the record update count: {}",
					inputRecordCount,updateRecordCount);
		}

		// If no records then no need to execute update
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

			if (!completedCostUpdateJob.getStatus()
					.equalsIgnoreCase(CostPriceJobStatus.COMPLETE.getMessageTemplate())) {

				String message = "FEI Cost Update task: " + chunkContext.getStepContext().getJobName()
						+ ", Non COMPLETE status: ["
						+ completedCostUpdateJob.getStatus()
						+ "] returned from executePriceBookCostUpdater";

				LOGGER.error(message);

				notificationService.message(message, SlackMessageType.WARNING);
			}

		} else {
			job.setStatus(CostPriceJobStatus.ERROR_VALIDATION.getMessageTemplate());
			feiPriceUpdateService.updateCostUploadJob(job);
			notificationService.message("FEI Price Update DataFlow task: "
					+ chunkContext.getStepContext().getJobName()
					+ ", Job validation error.  No records updated.", SlackMessageType.WARNING);
		}

		return RepeatStatus.FINISHED;
	}
}
