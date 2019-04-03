package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.inventory.model.VendorInventory;

@Mapper
public interface ManhattanInboundMapper {
	void createTemporaryManhattanInventoryTable(String jobKey);
	List<VendorInventory> getManhattanVendorInventory(String jobKey);
	List<VendorInventory> getManhattanVendorInventoryZeroes(String jobKey);
}
