package com.ferguson.cs.product.task.wiser.dao.reporter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.UniqueIdPricebookIdTuple;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

public interface WiserReporterDao {
	/**
	 * Get revenue categorization for each product
	 *
	 * @return map of unique ids to revenue category
	 */
	Map<Integer, ProductRevenueCategory> getProductRevenueCategorization();

	/**
	 * Get participation product sales. These are sales that won't be uploaded to the Integration.wiser.sale table.
	 *
	 * @param date
	 * @return
	 */
	List<WiserSale> getParticipationProductSales(Date date);

	/**
	 * Gets the current price of a product
	 *
	 * @param uniqueId
	 * @param pricebookId
	 * @return price of given product in given pricebook
	 */
	Double getCurrentPrice(Integer uniqueId, Integer pricebookId);

	/**
	 * Gets current price data for a list of product unique ids
	 *
	 * @param uniqueIds
	 * @param  partitionSize - number of uniqueids to query the database for at a time
	 */
	Map<UniqueIdPricebookIdTuple, Double> getCurrentPriceData(List<Integer> uniqueIds, int partitionSize);
}
