package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;

public interface ManhattanInboundDao {
	/**
	 * Get all Manhattan intake jobs that are loading or ready for processing
	 *
	 * @return list of manhattan intake jobs
	 */
	List<ManhattanIntakeJob> getManhattanIntakeJobs();

	/**
	 * Updates manhattan intake job status.
	 * @param manhattanIntakeJob
	 */
	void updateManhattanIntakeJobStatus(ManhattanIntakeJob manhattanIntakeJob);
}
