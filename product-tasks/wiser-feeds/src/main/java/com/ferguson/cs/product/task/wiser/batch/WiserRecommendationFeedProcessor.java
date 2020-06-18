package com.ferguson.cs.product.task.wiser.batch;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.ferguson.cs.product.task.wiser.WiserFeedSettings;
import com.ferguson.cs.product.task.wiser.model.CostUploadData;
import com.ferguson.cs.product.task.wiser.model.UniqueIdPricebookIdTuple;
import com.ferguson.cs.product.task.wiser.service.WiserService;

public class WiserRecommendationFeedProcessor implements ItemProcessor<CostUploadData, CostUploadData> {
	private WiserFeedSettings wiserFeedSettings;
	private List<Integer> recommendationUniqueIds;
	private WiserService wiserService;
	private Map<UniqueIdPricebookIdTuple, Double> oldCostMap;

	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
	}

	@Autowired
	public void setRecommendationUniqueIds(List<Integer> recommendationUniqueIds) {
		this.recommendationUniqueIds = recommendationUniqueIds;
	}

	@Autowired
	public void setWiserService(WiserService wiserService) {
		this.wiserService = wiserService;
	}


	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		if(!CollectionUtils.isEmpty(recommendationUniqueIds) && wiserService != null) {
			oldCostMap = wiserService.getCurrentPriceData(recommendationUniqueIds,1000);
		}
	}

	@Override
	public CostUploadData process(CostUploadData item) throws Exception {
		if(!validateRecommendationData(item)) {
			return null;
		}
		UniqueIdPricebookIdTuple uniqueIdPricebookIdTuple = new UniqueIdPricebookIdTuple(item.getUniqueId(),item.getPricebookId());
		item.setOldCost(oldCostMap.get(uniqueIdPricebookIdTuple));

		if(wiserFeedSettings != null && item.getOldCost() != null && item.getOldCost() > 0) {
			Double minPriceDifferenceRatio = wiserFeedSettings.getPriceMinPercentDifference() == null ? 0 : wiserFeedSettings.getPriceMinPercentDifference();
			Double maxPriceDifferenceRatio = wiserFeedSettings.getPriceMaxPercentDifference() == null ? Double.POSITIVE_INFINITY : wiserFeedSettings.getPriceMaxDifference();
			Double minPriceDifference = wiserFeedSettings.getPriceMinDifference() == null ? 0 : wiserFeedSettings.getPriceMinDifference();
			Double maxPriceDifference = wiserFeedSettings.getPriceMaxDifference() == null ? Double.POSITIVE_INFINITY : wiserFeedSettings.getPriceMaxDifference();
			Double priceDifferencePercentage = Math.abs((item.getCost()-item.getOldCost())/item.getOldCost());
			Double priceDifference = Math.abs(item.getCost()-item.getOldCost());


			if(priceDifferencePercentage.compareTo(minPriceDifferenceRatio) < 0 && priceDifference.compareTo(minPriceDifference) < 0) {
				return null;
			}

			if(priceDifferencePercentage.compareTo(maxPriceDifferenceRatio) > 0 || priceDifference.compareTo(maxPriceDifference) > 0) {
				return null;
			}
		}

		return item;
	}


	private boolean validateRecommendationData(CostUploadData item) {
		return item != null &&
				item.getCost() != null &&
				item.getCost() > 0 &&
				item.getPricebookId() != null &&
				item.getUniqueId() != null;
	}
}
