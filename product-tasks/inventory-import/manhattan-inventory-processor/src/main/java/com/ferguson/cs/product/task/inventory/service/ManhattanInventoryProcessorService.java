package com.ferguson.cs.product.task.inventory.service;

import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

public interface ManhattanInventoryProcessorService {
	/**
	 * Gets the most recent Manhattan inventory job that is either ready for processing or in the "loading" status but
	 * that has existed for longer than a configured timeout
	 *
	 * @param manhattanChannel channel to filter on
	 * @return ManhattanInventoryJob object if a matching one exists, null otherwise
	 */
	ManhattanInventoryJob getOldestReadyManhattanInventoryJob(ManhattanChannel manhattanChannel);

	/**
	 * Update manhattan inventory job data
	 *
	 * @param manhattanInventoryJob job updated
	 */
	void updateManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob);

	void deleteManhattanInventoryJobData(int manhattanInventoryJobId);
}
