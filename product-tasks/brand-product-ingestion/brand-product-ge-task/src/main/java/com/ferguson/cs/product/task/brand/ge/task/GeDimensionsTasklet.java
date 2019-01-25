package com.ferguson.cs.product.task.brand.ge.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.brand.ge.services.GeProductApiService;
import com.ferguson.cs.product.task.brand.model.SystemSource;
import com.ferguson.cs.product.task.brand.service.ProductDistributionService;
import com.ge_products.api.FilterValue;
import com.ge_products.api.GeProductDimension;
import com.ge_products.api.GeProductSearchCriteria;
import com.ge_products.api.GeProductSearchResult;

/**
 * This tasklet is used to execute the Dimension GE API
 * 
 * @author c-chandra
 *
 */
public class GeDimensionsTasklet  implements Tasklet {
	
	@Autowired
	private GeProductApiService geProductApiService;
	
	@Autowired
	protected ProductDistributionService productDistributionService;
	
	/*
	 * Stores the different navigation state search criteria
	 */
	private Stack<GeProductSearchCriteria> geProductSearchCriteriaStack = new Stack<>();
	
	
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		GeProductSearchResult result = geProductApiService.getDimensions("");
		SystemSource systemSource = new SystemSource();
		systemSource.setSourceName("GE");
		if (result != null && !result.getFilters().isEmpty()) {
			for (Map.Entry<String,GeProductDimension> filter: result.getFilters().entrySet()) {
				/*
				 * Get Active products filter.
				 * TODO: We need to fetch Obsolete products  ?
				 * 
				 */
				if (filter.getKey().equalsIgnoreCase("Obsolete")) {
					for (FilterValue filterValue : filter.getValue().getFilterValues()) {
						if ("FALSE".equalsIgnoreCase(filterValue.getLabel())) {
							systemSource.setActiveProductsFetched(filterValue.getCount());
							GeProductSearchCriteria geApiSearchCriteria = new GeProductSearchCriteria();
							geApiSearchCriteria.setStartIndex(0);	
							List<Long> dimensionFilter = new ArrayList<>();
							String dimensionNavState = filterValue.getNavigationState().replaceAll("[^\\d]", "" );
							if (!dimensionNavState.isEmpty()) {
								dimensionFilter.add(Long.parseLong(dimensionNavState));
							}
							geApiSearchCriteria.setNavDescriptors(dimensionFilter);
					 		geProductSearchCriteriaStack.push(geApiSearchCriteria);
						} else {
							systemSource.setObsoleteProductsFetched(filterValue.getCount());
						}
					}
				
				}
				
			}
		}
		
		productDistributionService.saveSystemSource(systemSource);
		
		
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("dimensionFilter", geProductSearchCriteriaStack );
		chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("systemSourceId", systemSource.getId() );
		
		return RepeatStatus.FINISHED;
		
		
	}
	
	
	
	
	
}
