package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Autowired;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

public class ManhattanZeroesDecider implements JobExecutionDecider {

	private ManhattanInventoryJob manhattanInventoryJob;

	@Autowired
	public void setManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob) {
		this.manhattanInventoryJob = manhattanInventoryJob;
	}


	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		if(manhattanInventoryJob.getId() != null && manhattanInventoryJob.getCurrentCount() >= manhattanInventoryJob.getTotalCount()) {
			return new FlowExecutionStatus("ZEROES");
		}

		return FlowExecutionStatus.COMPLETED;
	}
}
