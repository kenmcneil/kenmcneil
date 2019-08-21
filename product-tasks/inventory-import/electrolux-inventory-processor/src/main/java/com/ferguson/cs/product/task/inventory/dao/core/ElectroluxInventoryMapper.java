package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ferguson.cs.product.task.inventory.model.ElectroluxSkuVendorData;

@Mapper
public interface ElectroluxInventoryMapper {
	List<ElectroluxSkuVendorData> getElectroluxSkus(Integer vendorUid);
}
