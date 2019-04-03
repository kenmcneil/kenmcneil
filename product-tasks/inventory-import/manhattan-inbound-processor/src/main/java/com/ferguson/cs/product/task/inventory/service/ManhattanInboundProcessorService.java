package com.ferguson.cs.product.task.inventory.service;

public interface ManhattanInboundProcessorService {
	/**
	 * Creates a temp table for manhattan data
	 *
	 * @param jobKey for temp table
	 */
	 void createManhattanTempTable(String jobKey);
}
