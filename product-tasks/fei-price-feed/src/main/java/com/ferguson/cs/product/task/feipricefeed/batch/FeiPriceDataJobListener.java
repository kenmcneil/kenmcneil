package com.ferguson.cs.product.task.feipricefeed.batch;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.ferguson.cs.product.task.feipricefeed.FeiPriceSettings;
import com.ferguson.cs.product.task.feipricefeed.service.FeiPriceService;

public class FeiPriceDataJobListener implements JobExecutionListener {
	private static final Logger LOG = LoggerFactory.getLogger(FeiPriceDataJobListener.class);
	private final FeiPriceService feiPriceService;
	private final FeiPriceSettings feiPriceSettings;

	public FeiPriceDataJobListener(FeiPriceService feiPriceService, FeiPriceSettings feiPriceSettings) {
		this.feiPriceService = feiPriceService;
		this.feiPriceSettings = feiPriceSettings;
	}


	@Override
	public void beforeJob(JobExecution jobExecution) {
		//No implementation needed
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName();
		if(jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
			feiPriceService.incrementNumberOfRunsToday(jobName);
		}

		try {
			Resource[] files = new PathMatchingResourcePatternResolver().getResources("file:" + feiPriceSettings.getTemporaryFilePath() + "*");
			for(Resource file : files) {
				FileUtils.deleteQuietly(file.getFile());
			}
		} catch (IOException e) {
			LOG.error("Failed to clean up temporary CSVs. Exception: {}",e.toString());
		}

	}
}
