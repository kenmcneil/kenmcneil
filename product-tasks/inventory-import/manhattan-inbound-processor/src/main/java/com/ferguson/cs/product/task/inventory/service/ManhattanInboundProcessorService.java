package com.ferguson.cs.product.task.inventory.service;

import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;

public interface ManhattanInboundProcessorService {
	/**
	 * Gets the most recent Manhattan intake job that is either ready for processing or in the "loading" status but
	 * that has existed for longer than a configured timeout
	 *
	 * @return ManhattanIntakeJob object if a matching one exists, null otherwise
	 */
	ManhattanIntakeJob getNewestReadyManhattanIntakeJob();

	/**
	 * Update manhattan intake job data
	 *
	 * @param manhattanIntakeJob job updated
	 */
	void updateManhattanIntakeJob(ManhattanIntakeJob manhattanIntakeJob);
}
