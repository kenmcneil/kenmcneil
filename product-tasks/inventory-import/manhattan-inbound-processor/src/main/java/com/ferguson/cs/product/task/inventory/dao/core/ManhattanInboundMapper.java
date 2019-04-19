package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ferguson.cs.product.task.inventory.model.VendorInventory;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;

@Mapper
public interface ManhattanInboundMapper {
	List<VendorInventory> getManhattanVendorInventory(@Param("transactionNumber") String transactionNumber);
	List<VendorInventory> getManhattanVendorInventoryZeroes(@Param("transactionNumber") String transactionNumber);
	List<ManhattanIntakeJob> getManhattanIntakeJobs();
	void updateManhattanIntakeJobStatus(ManhattanIntakeJob manhattanIntakeJob);
}
