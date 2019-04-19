package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Autowired;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJobStatus;

public class ManhattanZeroesDecider implements JobExecutionDecider {

	private ManhattanIntakeJob manhattanIntakeJob;

	@Autowired
	public void setManhattanIntakeJob(ManhattanIntakeJob manhattanIntakeJob) {
		this.manhattanIntakeJob = manhattanIntakeJob;
	}


	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		if(manhattanIntakeJob.getId() != null && manhattanIntakeJob.getManhattanIntakeJobStatus() == ManhattanIntakeJobStatus.READY_FOR_PROCESSING) {
			return new FlowExecutionStatus("ZEROES");
		}

		return FlowExecutionStatus.COMPLETED;
	}
}
