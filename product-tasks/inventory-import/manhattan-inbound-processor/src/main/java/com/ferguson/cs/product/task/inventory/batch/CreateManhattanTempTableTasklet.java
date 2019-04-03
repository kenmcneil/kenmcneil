package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.inventory.service.ManhattanInboundProcessorService;

public class CreateManhattanTempTableTasklet implements Tasklet {

	private ManhattanInboundProcessorService manhattanInboundProcessorService;

	@Autowired
	public void setManhattanInboundProcessorService(ManhattanInboundProcessorService manhattanInboundProcessorService) {
		this.manhattanInboundProcessorService = manhattanInboundProcessorService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String jobKey = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getString("jobKey");
		manhattanInboundProcessorService.createManhattanTempTable(jobKey);
		return RepeatStatus.FINISHED;
	}
}
