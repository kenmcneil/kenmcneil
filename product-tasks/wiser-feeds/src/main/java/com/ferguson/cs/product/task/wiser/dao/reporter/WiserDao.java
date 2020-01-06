package com.ferguson.cs.product.task.wiser.dao.reporter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ferguson.cs.product.task.wiser.model.ProductRevenueCategory;
import com.ferguson.cs.product.task.wiser.model.WiserSale;

public interface WiserDao {
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
}
