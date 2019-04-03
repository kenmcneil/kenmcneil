package com.ferguson.cs.product.task.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.dao.core.ManhattanInboundDao;

@Service
public class ManhattanInboundProcessorServiceImpl implements ManhattanInboundProcessorService {

	private ManhattanInboundDao manhattanInboundDao;


	@Autowired
	public void setManhattanInboundDao(ManhattanInboundDao manhattanInboundDao) {
		this.manhattanInboundDao = manhattanInboundDao;
	}


	@Override
	public void createManhattanTempTable(String jobKey) {
		manhattanInboundDao.createTemporaryManhattanInventoryTable(jobKey);
	}
}
