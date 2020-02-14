package com.ferguson.cs.product.task.inventory.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJobStatus;
import com.ferguson.cs.product.task.inventory.service.ManhattanInventoryProcessorService;

/**
 * Job listener that attempts to load Manhattan inventory job data before job and updates job status when the job ends.
 */
public class ManhattanVendorInventoryJobListener implements JobExecutionListener {

	private ManhattanInventoryProcessorService manhattanInventoryProcessorService;
	private ManhattanInventoryJob manhattanInventoryJob;
	private ManhattanChannel manhattanChannel;

	public ManhattanVendorInventoryJobListener(ManhattanChannel manhattanChannel) {
		this.manhattanChannel = manhattanChannel;
	}

	@Autowired
	public void setManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob) {
		this.manhattanInventoryJob = manhattanInventoryJob;
	}

	@Autowired
	public void setManhattanInventoryProcessorService(ManhattanInventoryProcessorService manhattanInventoryProcessorService) {
		this.manhattanInventoryProcessorService = manhattanInventoryProcessorService;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		ManhattanInventoryJob manhattanInventoryJob = manhattanInventoryProcessorService
				.getOldestReadyManhattanInventoryJob(manhattanChannel);

		if (manhattanInventoryJob != null) {
			this.manhattanInventoryJob.setId(manhattanInventoryJob.getId());
			this.manhattanInventoryJob.setCreatedDateTime(manhattanInventoryJob.getCreatedDateTime());
			this.manhattanInventoryJob.setTotalCount(manhattanInventoryJob.getTotalCount());
			this.manhattanInventoryJob.setTransactionNumber(manhattanInventoryJob.getTransactionNumber());
			this.manhattanInventoryJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.PROCESSING);
			this.manhattanInventoryJob.setManhattanChannel(manhattanInventoryJob.getManhattanChannel());
			this.manhattanInventoryJob.setDataIsComplete(manhattanInventoryJob.getDataIsComplete());
			manhattanInventoryProcessorService.updateManhattanInventoryJob(manhattanInventoryJob);
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (manhattanInventoryJob.getId() != null) {
			if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
				manhattanInventoryJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.COMPLETE);
				manhattanInventoryProcessorService.deleteManhattanInventoryJobData(manhattanInventoryJob.getId());
			} else if (jobExecution.getExitStatus().equals(ExitStatus.FAILED)) {
				manhattanInventoryJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.FAILED);
			}
			manhattanInventoryProcessorService.updateManhattanInventoryJob(manhattanInventoryJob);
			FileUtils.deleteQuietly(new File(jobExecution.getExecutionContext().getString("filePath")));
		} else {
			jobExecution.setExitStatus(new ExitStatus("COMPLETED(NOOP)"));
			jobExecution.setStatus(BatchStatus.COMPLETED);
		}
	}
}
