package com.ferguson.cs.product.task.inventory.dao.pdm;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ferguson.cs.product.task.inventory.model.InventoryImportJobError;
import com.ferguson.cs.product.task.inventory.model.InventoryImportJobLog;

@Mapper
public interface InventoryImportJobMapper {
	void insertInventoryImportJobLog(InventoryImportJobLog inventoryImportJobLog);
	void insertFtpInventoryImportJobDetails(@Param("inventoryImportJobLogId") int inventoryImportJobId, @Param("filename") String filename, @Param("isSftp") boolean isSftp);
	void updateInventoryImportJobLog(InventoryImportJobLog inventoryImportJobLog);
	void insertInventoryImportJobError(InventoryImportJobError inventoryImportJobError);
}
