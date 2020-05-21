package com.ferguson.cs.product.task.dy.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;


import com.ferguson.cs.product.task.dy.DyFeedSettings;

public class DynamicYieldReadListener implements StepExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicYieldReadListener.class);
    private DyFeedSettings dyFeedSettings;

    public DynamicYieldReadListener(DyFeedSettings dyFeedSettings) {
        this.dyFeedSettings = dyFeedSettings;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getReadCount() < dyFeedSettings.getMinimumRecordCount()) {
            LOGGER.error("Row Count < 1.6m rows!");
            return new ExitStatus(ExitStatus.FAILED.getExitCode(), "Row Count < 1.6m rows!");
        }

       return stepExecution.getExitStatus();
    }
}
