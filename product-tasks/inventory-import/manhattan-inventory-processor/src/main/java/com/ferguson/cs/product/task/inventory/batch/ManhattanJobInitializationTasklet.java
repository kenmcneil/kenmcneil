package com.ferguson.cs.product.task.inventory.batch;

import java.io.File;
import org.apache.commons.io.FileUtils;
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
	private File oldFile;


	public ManhattanJobInitializationTasklet(ManhattanInventoryJob manhattanInventoryJob,String filePath) {
		this.manhattanInventoryJob = manhattanInventoryJob;
		if(filePath != null) {
			oldFile = new File(filePath);
		}
	}

	@Autowired
	public void setManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob) {
		this.manhattanInventoryJob = manhattanInventoryJob;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		if(manhattanInventoryJob.getId() == null) {
			contribution.setExitStatus(ExitStatus.NOOP);
		} else {
			//In the event that something went catastrophically wrong on a previous run, the old file will still exist
			//This is to ensure that a catastrophic failure doesn't cause problems for later runs
			FileUtils.deleteQuietly(oldFile);
		}
		return RepeatStatus.FINISHED;
	}
}
