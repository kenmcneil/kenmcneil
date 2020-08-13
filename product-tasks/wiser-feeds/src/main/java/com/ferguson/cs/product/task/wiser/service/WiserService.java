package com.ferguson.cs.product.task.wiser.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ferguson.cs.product.task.wiser.model.ProductConversionBucket;
import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.RecommendationJobLog;
import com.ferguson.cs.product.task.wiser.model.UniqueIdPricebookIdTuple;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

public interface WiserService {


	/**
	 * Gets date that a given feed last ran
	 * @return date last ran
	 */
	Date getLastRanDate(String jobName);


	/**
	 * Gets all product data hashes
	 *
	 * @return a list of all current product data hashes
	 */
	List<ProductDataHash> getAllProductDataHashes();

	/**
	 * Gets all active or recently modified wiser sales
	 *
	 * @param date	Start of date range
	 * @return wiser sales modified recently or currently active
	 */
	List<WiserSale> getWiserSales(Date date);

	/**
	 * Get a map of all product revenue categorizations
	 *
	 * @return
	 */
	Map<Integer, ProductRevenueCategory> getProductRevenueCategorization();

	/**
	 * Get a map of all product conversion buckets
	 *
	 * @return
	 */
	Map<Integer, ProductConversionBucket> getProductConversionBuckets();

	/**
	 * Get conversion bucket for product
	 *
	 * @param productUniqueId
	 * @return string conversion bucket value
	 */
	String getProductConversionBucket(Integer productUniqueId);

	/**
	 * Determines if a given wiser sale object indicates that a product is on sale starting from a given date
	 *
	 * @param wiserSale	wiser sale object
	 * @param date	starting date
	 * @return true if item is on sale, false otherwise
	 */
	boolean isItemPromo(WiserSale wiserSale, Date date);

	/**
	 * Delete all product data hashes
	 */
	void truncateProductDataHashes();

	/**
	 * Populate product revenue categorization table
	 */
	void populateProductRevenueCategorization();

	/**
	 * Gets current price for product in pricebook
	 * @param uniqueId
	 * @param pricebookId
	 * @return price in pricebook for product
	 */
	Double getCurrentPrice(Integer uniqueId, Integer pricebookId);

	/**
	 * Gets current price data for a list of product unique ids
	 *
	 * @param uniqueIds
	 * @param  partitionSize - number of uniqueids to query the database for at a time
	 */
	Map<UniqueIdPricebookIdTuple, Double> getCurrentPriceData(List<Integer> uniqueIds, int partitionSize);

	void insertRecommendationJobLog(RecommendationJobLog recommendationJobLog);

	void deleteTodaysRecommendationJobLogs();
}
