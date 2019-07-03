package com.ferguson.cs.product.task.inventory.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

/**
 * Tasklet class that determines whether there is a manhattan inventory job loaded for processing. Returns exit status NOOP
 * if there isn't, FINISHED if there is.
 */
public class ManhattanJobInitializationTasklet implements Tasklet {
	private ManhattanInventoryJob manhattanInventoryJob;

	@Autowired
	public void setManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob) {
		this.manhattanInventoryJob = manhattanInventoryJob;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(manhattanInventoryJob.getId() == null) {
			contribution.setExitStatus(ExitStatus.NOOP);
		}
		return RepeatStatus.FINISHED;
	}
}
