package com.ferguson.cs.product.task.wiser.batch;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.task.batch.util.JobRepositoryHelper;

public class WiserRetryableJobTasklet implements Tasklet {

	private final JobRepositoryHelper jobRepositoryHelper;

	public WiserRetryableJobTasklet(JobRepositoryHelper jobRepositoryHelper) {
		this.jobRepositoryHelper = jobRepositoryHelper;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		JobExecution jobExecution = jobRepositoryHelper
				.getLastJobExecution(chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance()
						.getJobName(), ExitStatus.COMPLETED);

		Date lastRanDate = null;
		if(jobExecution != null) {
			lastRanDate = jobExecution.getEndTime();
		}
		if (lastRanDate != null && DateUtils.isSameDay(new Date(), lastRanDate)) {
			contribution.setExitStatus(ExitStatus.NOOP);
		}
		return RepeatStatus.FINISHED;
	}
}
