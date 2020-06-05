package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class MpnMpidMismatchMpidBackfillDecider implements JobExecutionDecider {

	public static final String CONTINUE = "CONTINUE";
	public static final String NO_MISSING_RECORDS = "NO_MISSING_RECORDS";

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		if (stepExecution == null) {
			throw new IllegalArgumentException("stepExecution cannot be null");
		}

		// Parent step should have checked the input files and put a list of them in the executionContext.
		if (stepExecution.getWriteCount() > 0) {
			return new FlowExecutionStatus(CONTINUE);
		} else {
			return new FlowExecutionStatus(NO_MISSING_RECORDS);
		}
	}

}
