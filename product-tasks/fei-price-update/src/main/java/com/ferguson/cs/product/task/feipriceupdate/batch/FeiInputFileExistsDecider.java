package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.ferguson.cs.product.task.feipriceupdate.model.PricebookType;

public class FeiInputFileExistsDecider implements JobExecutionDecider {

	public static final String CONTINUE = "CONTINUE";
	public static final String NO_INPUT_FILE = "NO_INPUT_FILE";
	private final PricebookType pricebookType;

	public FeiInputFileExistsDecider(PricebookType priceBookType) {
		this.pricebookType = priceBookType;
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		if (stepExecution == null) {
			throw new IllegalArgumentException("stepExecution cannot be null");
		}

		// If no type is defined we are checking for the existence of any input file. In this case we will stop the job altogether.
		// This decider is leveraged several times in the job config to control flow based the type of file we need to process.
		if (pricebookType == null) {
			if (jobExecution.getExecutionContext().containsKey(FeiCreatePriceUpdateTempTableTasklet.PB1_INPUT_FILE) ||
					jobExecution.getExecutionContext().containsKey(FeiCreatePriceUpdateTempTableTasklet.PB22_INPUT_FILE)) {
				return new FlowExecutionStatus(CONTINUE);
			}
		}

		// Parent step should have checked the input files and put a list of them in the executionContext.
		if (pricebookType == PricebookType.PB1 && jobExecution.getExecutionContext().containsKey(FeiCreatePriceUpdateTempTableTasklet.PB1_INPUT_FILE)) {
			return new FlowExecutionStatus(CONTINUE);
		}

		if (pricebookType == PricebookType.PB22 && jobExecution.getExecutionContext().containsKey(FeiCreatePriceUpdateTempTableTasklet.PB22_INPUT_FILE)) {
			return new FlowExecutionStatus(CONTINUE);
		}

		return new FlowExecutionStatus(NO_INPUT_FILE);
	}
}
