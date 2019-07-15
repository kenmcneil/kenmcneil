package com.ferguson.cs.product.task.brand.ge.task;

import java.util.Stack;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class GeProductsJobDecider implements JobExecutionDecider {

	public static final String COMPLETED = "COMPLETED";
	public static final String CONTINUE = "CONTINUE";

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		Stack<String> dimensionFilter = (Stack)jobExecution.getExecutionContext().get("dimensionFilter");
		if (!dimensionFilter.isEmpty()) {
			return new FlowExecutionStatus(CONTINUE);
		} else {
			return new FlowExecutionStatus(COMPLETED);
		}
	}

}
