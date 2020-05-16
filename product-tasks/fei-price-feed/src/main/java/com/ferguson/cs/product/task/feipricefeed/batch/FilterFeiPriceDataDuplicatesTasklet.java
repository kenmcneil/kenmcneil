package com.ferguson.cs.product.task.feipricefeed.batch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.feipricefeed.FeiPriceSettings;
import com.ferguson.cs.product.task.feipricefeed.model.FeiPriceData;
import com.google.common.collect.Multimap;

public class FilterFeiPriceDataDuplicatesTasklet implements Tasklet {
	private final Multimap<Integer, FeiPriceData> feiPriceDataMultimap;
	private FeiPriceSettings feiPriceSettings;

	public FilterFeiPriceDataDuplicatesTasklet(Multimap<Integer, FeiPriceData> feiPriceDataMultimap) {
		this.feiPriceDataMultimap = feiPriceDataMultimap;
	}

	@Autowired
	public void setFeiPriceSettings(FeiPriceSettings feiPriceSettings) {
		this.feiPriceSettings = feiPriceSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Map<Integer, List<FeiPriceData>> duplicates = feiPriceDataMultimap.asMap().entrySet().stream().filter(p->p.getValue().size() > 1).collect(Collectors.toMap(Map.Entry::getKey, p->new ArrayList<>(p.getValue())));

		Map<Integer, FeiPriceData> winners = new HashMap<>();

		for(Map.Entry<Integer, List<FeiPriceData>> entry : duplicates.entrySet()) {
			FeiPriceData winner = null;
			winners.put(entry.getKey(), Collections.max(entry.getValue(), (o1, o2) -> {
				if(o1 == null && o2 == null) {
					return 0;
				}

				//Prioritize existing objects over non-existent ones
				if(o1 == null) {
					return -1;
				}

				if(o2 == null) {
					return 1;
				}

				if(o1.equals(o2)) {
					return 0;
				}

				//Prefer stock products to nonstock
				if(o1.getStatus().equalsIgnoreCase("stock") && !o2.getStatus().equalsIgnoreCase("stock")){
					return 1;
				} else if (o2.getStatus().equalsIgnoreCase("stock")) {
					return -1;
				}

				//Prefer non-whitelabel to whitelabel
				if(feiPriceSettings != null && !CollectionUtils.isEmpty(feiPriceSettings.getWhiteLabelBrands())) {
					if (feiPriceSettings.getWhiteLabelBrands().contains(o1.getBrand()) && !feiPriceSettings
							.getWhiteLabelBrands().contains(o2.getBrand())) {
						return -1;
					} else if (feiPriceSettings.getWhiteLabelBrands().contains(o2.getBrand())) {
						return 1;
					}
				}

				//Doesn't meet any of the criteria to pick a higher priority
				return 0;
			}));
		}

		for(Map.Entry<Integer,FeiPriceData> winner : winners.entrySet()) {
			feiPriceDataMultimap.replaceValues(winner.getKey(),Collections.singletonList(winner.getValue()));
		}

		ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().ge

		return RepeatStatus.FINISHED;
	}
}
