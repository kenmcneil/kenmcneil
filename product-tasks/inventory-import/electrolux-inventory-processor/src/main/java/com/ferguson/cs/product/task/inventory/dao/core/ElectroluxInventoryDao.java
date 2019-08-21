package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import com.ferguson.cs.product.task.inventory.model.ElectroluxSkuVendorData;

public interface ElectroluxInventoryDao {
	List<ElectroluxSkuVendorData> getElectroluxSkus(Integer vendorUid);
}
