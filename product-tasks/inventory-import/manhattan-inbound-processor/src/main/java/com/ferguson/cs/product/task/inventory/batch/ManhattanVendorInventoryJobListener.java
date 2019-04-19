package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJobStatus;
import com.ferguson.cs.product.task.inventory.service.ManhattanInboundProcessorService;

/**
 * Job listener that attempts to load Manhattan intake job data before job and updates job status when the job ends.
 */
public class ManhattanVendorInventoryJobListener implements JobExecutionListener{

	private ManhattanInboundProcessorService manhattanInboundProcessorService;
	private ManhattanIntakeJob manhattanIntakeJob;

	@Autowired
	public void setManhattanIntakeJob(ManhattanIntakeJob manhattanIntakeJob) {
		this.manhattanIntakeJob = manhattanIntakeJob;
	}

	@Autowired
	public void setManhattanInboundProcessorService(ManhattanInboundProcessorService manhattanInboundProcessorService) {
		this.manhattanInboundProcessorService = manhattanInboundProcessorService;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		ManhattanIntakeJob manhattanIntakeJob = manhattanInboundProcessorService.getNewestReadyManhattanIntakeJob();

		if(manhattanIntakeJob != null) {
			this.manhattanIntakeJob.setId(manhattanIntakeJob.getId());
			this.manhattanIntakeJob.setCreatedDateTime(manhattanIntakeJob.getCreatedDateTime());
			this.manhattanIntakeJob.setCurrentCount(manhattanIntakeJob.getCurrentCount());
			this.manhattanIntakeJob.setTotalCount(manhattanIntakeJob.getTotalCount());
			this.manhattanIntakeJob.setTransactionNumber(manhattanIntakeJob.getTransactionNumber());
			this.manhattanIntakeJob.setManhattanIntakeJobStatus(manhattanIntakeJob.getManhattanIntakeJobStatus());
		}
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(manhattanIntakeJob.getId() != null) {
			if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
				manhattanIntakeJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.COMPLETE);
			} else if (jobExecution.getExitStatus().equals(ExitStatus.FAILED)) {
				manhattanIntakeJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.FAILED);
			}
			manhattanInboundProcessorService.updateManhattanIntakeJob(manhattanIntakeJob);
		} else {
			jobExecution.setExitStatus(new ExitStatus("COMPLETED(NOOP)"));
			jobExecution.setStatus(BatchStatus.COMPLETED);
		}
	}

}
