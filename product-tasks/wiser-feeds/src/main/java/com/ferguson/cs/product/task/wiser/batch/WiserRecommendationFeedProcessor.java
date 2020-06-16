package com.ferguson.cs.product.task.wiser.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.ferguson.cs.product.task.wiser.WiserFeedSettings;
import com.ferguson.cs.product.task.wiser.model.WiserRecommendationData;

public class WiserRecommendationFeedProcessor implements ItemProcessor<WiserRecommendationData,WiserRecommendationData> {
	private WiserFeedSettings wiserFeedSettings;

	@Autowired
	public void setWiserFeedSettings(WiserFeedSettings wiserFeedSettings) {
		this.wiserFeedSettings = wiserFeedSettings;
	}



	@Override
	public WiserRecommendationData process(WiserRecommendationData item) throws Exception {
		if(!validateRecommendationData(item)) {
			return null;
		}

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


	private boolean validateRecommendationData(WiserRecommendationData item) {
		return item != null &&
				item.getCost() != null &&
				item.getCost() > 0 &&
				item.getPricebookId() != null &&
				item.getUniqueId() != null;
	}
}
