package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryLocationData;

public interface ManhattanInventoryDao {
	/**
	 * Get all Manhattan inventory jobs that are loading or ready for processing
	 *
	 * @param manhattanChannel channel to filter by
	 * @return list of manhattan inventory jobs
	 */
	List<ManhattanInventoryJob> getLoadingManhattanInventoryJobs(ManhattanChannel manhattanChannel);

	/**
	 * Updates manhattan inventory job status.
	 *
	 * @param manhattanInventoryJob
	 */
	void updateManhattanInventoryJobStatus(ManhattanInventoryJob manhattanInventoryJob);

	/**
	 * Deletes inventory data related to job
	 *
	 * @param manhattanInventoryJobId id of job to be cleaned up
	 */
	void deleteManhattanInventoryJobData(int manhattanInventoryJobId);

	/**
	 * Gets manhattan inventory location data related to job id
	 * @param manhattanInventoryJobId
	 * @return manhattan inventory location data for job id and current count
	 */
	List<ManhattanInventoryLocationData> getManhattanInventoryLocationDataForJob(Integer manhattanInventoryJobId);
}
