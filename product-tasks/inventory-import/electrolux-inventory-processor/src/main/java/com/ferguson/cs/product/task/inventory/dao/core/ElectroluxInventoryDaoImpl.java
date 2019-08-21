package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.inventory.model.ElectroluxSkuVendorData;

@Repository
public class ElectroluxInventoryDaoImpl implements ElectroluxInventoryDao {

	private ElectroluxInventoryMapper electroluxInventoryMapper;

	@Autowired
	public void setElectroluxInventoryMapper(ElectroluxInventoryMapper electroluxInventoryMapper) {
		this.electroluxInventoryMapper = electroluxInventoryMapper;
	}

	@Override
	public List<ElectroluxSkuVendorData> getElectroluxSkus(Integer vendorUid) {
		return electroluxInventoryMapper.getElectroluxSkus(vendorUid);
	}
}
