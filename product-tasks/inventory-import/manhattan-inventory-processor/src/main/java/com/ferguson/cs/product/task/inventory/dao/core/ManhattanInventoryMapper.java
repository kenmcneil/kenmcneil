package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryLocationData;

@Mapper
public interface ManhattanInventoryMapper {
	List<VendorInventory> getManhattanVendorInventory(@Param("transactionNumber") String transactionNumber);

	List<VendorInventory> getManhattanVendorInventoryZeroes(@Param("transactionNumber") String transactionNumber);

	List<VendorInventory> getFilteredManhattanVendorInventory(@Param("transactionNumber") String transactionNumber);

	List<ManhattanInventoryJob> getLoadingManhattanInventoryJobs(@Param("manhattanChannel") ManhattanChannel manhattanChannel);

	void updateManhattanInventoryJobStatus(ManhattanInventoryJob manhattanInventoryJob);

	void deleteManhattanInventoryJobData(int manhattanInventoryJobId);

	List<ManhattanInventoryLocationData> getManhattanInventoryLocationDataForJob(@Param("manhattanInventoryJobId") Integer manhattanInventoryJobId);
}
