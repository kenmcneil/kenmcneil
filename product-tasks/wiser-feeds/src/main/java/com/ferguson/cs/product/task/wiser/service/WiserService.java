package com.ferguson.cs.product.task.wiser.service;

import java.util.Date;
import java.util.List;

import com.ferguson.cs.product.task.wiser.model.ProductDataHash;
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
	 * Determines if a given wiser sale object indicates that a product is on sale starting from a given date
	 *
	 * @param wiserSale	wiser sale object
	 * @param date	starting date
	 * @return true if item is on sale, false otherwise
	 */
	boolean isItemPromo(WiserSale wiserSale, Date date);

	void truncateProductDataHashes();
}
