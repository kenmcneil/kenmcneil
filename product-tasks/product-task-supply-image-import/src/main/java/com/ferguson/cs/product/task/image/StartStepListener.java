package com.ferguson.cs.product.task.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class StartStepListener implements StepExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartStepListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		LOGGER.info("StartStepListener beforeStep invoked ...");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		LOGGER.info("StartStepListener afterStep invoked ...");

		LOGGER.info(String.valueOf(System.lineSeparator() + stepExecution.getExitStatus()));

		LOGGER.info(String.valueOf(stepExecution.getFailureExceptions().size()) + System.lineSeparator());

		if (ExitStatus.FAILED.getExitCode().equals(stepExecution.getExitStatus().getExitCode())) {

			LOGGER.error("Start step completed with errors.");

		} else {

			LOGGER.info(String.format("Start step completed with out errors .... exit-status=%s",
					stepExecution.getExitStatus()));

		}

		return null;

	}

}