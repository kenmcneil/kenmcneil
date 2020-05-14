package com.ferguson.cs.product.task.feipriceupdate.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class FeiInputFileProcessorListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// Not implemented
	}

	/*
	 * Need the read count for the Cost Uploader Job creation/execution step for
	 * logging
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		int readCount = stepExecution.getReadCount();
		stepExecution.getJobExecution().getExecutionContext().put("READ_COUNT", readCount);
		return null;
	}
}
