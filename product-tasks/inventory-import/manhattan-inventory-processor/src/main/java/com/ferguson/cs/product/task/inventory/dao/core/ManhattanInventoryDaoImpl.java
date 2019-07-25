package com.ferguson.cs.product.task.inventory.dao.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryLocationData;

@Repository
public class ManhattanInventoryDaoImpl implements ManhattanInventoryDao {

	private ManhattanInventoryMapper manhattanInventoryMapper;

	@Autowired
	public void setManhattanInventoryMapper(ManhattanInventoryMapper manhattanInventoryMapper) {
		this.manhattanInventoryMapper = manhattanInventoryMapper;
	}

	@Override
	public List<ManhattanInventoryJob> getLoadingManhattanInventoryJobs(ManhattanChannel manhattanChannel) {
		return manhattanInventoryMapper.getLoadingManhattanInventoryJobs(manhattanChannel);
	}

	@Override
	public void updateManhattanInventoryJobStatus(ManhattanInventoryJob manhattanInventoryJob) {
		manhattanInventoryMapper.updateManhattanInventoryJobStatus(manhattanInventoryJob);
	}

	@Override
	public void deleteManhattanInventoryJobData(int manhattanInventoryJobId) {
		manhattanInventoryMapper.deleteManhattanInventoryJobData(manhattanInventoryJobId);
	}

	@Override
	public List<ManhattanInventoryLocationData> getManhattanInventoryLocationDataForJob(Integer manhattanInventoryJobId) {
		return manhattanInventoryMapper.getManhattanInventoryLocationDataForJob(manhattanInventoryJobId);
	}
}
