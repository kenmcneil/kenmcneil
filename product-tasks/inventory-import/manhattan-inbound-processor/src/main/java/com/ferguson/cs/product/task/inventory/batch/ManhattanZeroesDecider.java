package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class ManhattanZeroesDecider implements JobExecutionDecider {
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		int totalJsonFileCount = jobExecution.getExecutionContext().getInt("totalJsonFileCount");
		int currentJsonFileCount = jobExecution.getExecutionContext().getInt("currentJsonFileCount");

		if(currentJsonFileCount == totalJsonFileCount) {
			return new FlowExecutionStatus("ZEROES");
		}

		return FlowExecutionStatus.COMPLETED;
	}
}
