package com.ferguson.cs.product.task.brand.ge.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.brand.service.ProductDistributionService;

/**
 * This tasklet is used to delete inactive products
 * 
 * @author c-chandra
 *
 */
public class GeDeleteStaleProductsTasklet  implements Tasklet {
	
	
	@Autowired
	protected ProductDistributionService productDistributionService;
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Integer systemSourceId = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
				.getInt("systemSourceId");
		productDistributionService.deleteStaleProducts(systemSourceId);
		return RepeatStatus.FINISHED;
		
		
	}
	
	
	
	
	
}
