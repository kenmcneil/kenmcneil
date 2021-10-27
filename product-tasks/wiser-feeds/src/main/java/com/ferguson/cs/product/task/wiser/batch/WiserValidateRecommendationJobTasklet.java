package com.ferguson.cs.product.task.wiser.batch;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.wiser.WiserFeedTaskConfiguration;
import com.ferguson.cs.product.task.wiser.model.RecommendationJobFailureCause;
import com.ferguson.cs.product.task.wiser.model.RecommendationJobLog;
import com.ferguson.cs.product.task.wiser.service.WiserService;
import com.ferguson.cs.task.batch.util.JobRepositoryHelper;

public class WiserValidateRecommendationJobTasklet implements Tasklet {

	private static final Logger LOG = LoggerFactory.getLogger(WiserValidateRecommendationJobTasklet.class);

	private final WiserService wiserService;
	private final JobRepositoryHelper jobRepositoryHelper;

	public WiserValidateRecommendationJobTasklet(WiserService wiserService, JobRepositoryHelper jobRepositoryHelper) {
		this.wiserService = wiserService;
		this.jobRepositoryHelper = jobRepositoryHelper;
	}


	@Override
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
		JobExecution jobExecution = null;

		try {
			jobExecution = jobRepositoryHelper
					.getLastJobExecution(WiserFeedTaskConfiguration.RECOMMENDATION_JOB_NAME);
		} catch (Exception e) {
			LOG.error("Failed to retrieve previous job execution data", e);
		}

		if(jobExecution == null || !(DateUtils.isSameDay(new Date(), jobExecution.getEndTime()))) {
			RecommendationJobLog recommendationJobLog = new RecommendationJobLog();
			recommendationJobLog.setRunDateTime(new Date());
			recommendationJobLog.setRecommendationJobFailureCause(RecommendationJobFailureCause.INTERNAL);
			recommendationJobLog.setSuccessful(false);
			wiserService.insertRecommendationJobLog(recommendationJobLog);
		}
		return RepeatStatus.FINISHED;
	}
}
