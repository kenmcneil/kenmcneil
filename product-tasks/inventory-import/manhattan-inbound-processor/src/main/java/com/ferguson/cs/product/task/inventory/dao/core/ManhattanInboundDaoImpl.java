package com.ferguson.cs.product.task.inventory.dao.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ManhattanInboundDaoImpl implements ManhattanInboundDao {

	private ManhattanInboundMapper manhattanInboundMapper;

	@Autowired
	public void setManhattanInboundMapper(ManhattanInboundMapper manhattanInboundMapper) {
		this.manhattanInboundMapper = manhattanInboundMapper;
	}

	@Override
	public void createTemporaryManhattanInventoryTable(String jobKey) {
		manhattanInboundMapper.createTemporaryManhattanInventoryTable(jobKey);
	}
}
