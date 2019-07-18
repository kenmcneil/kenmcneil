package com.ferguson.cs.product.task.inventory.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.ManhattanInboundSettings;
import com.ferguson.cs.product.task.inventory.dao.core.ManhattanInventoryDao;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;

@Service
public class ManhattanInventoryProcessorServiceImpl implements ManhattanInventoryProcessorService {

	private ManhattanInventoryDao manhattanInventoryDao;
	private ManhattanInboundSettings manhattanInboundSettings;

	@Autowired
	public void setManhattanInventoryDao(ManhattanInventoryDao manhattanInventoryDao) {
		this.manhattanInventoryDao = manhattanInventoryDao;
	}

	@Autowired
	public void setManhattanInboundSettings(ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@Override
	public ManhattanInventoryJob getOldestReadyManhattanInventoryJob(ManhattanChannel manhattanChannel) {
		List<ManhattanInventoryJob> manhattanInventoryJobs = manhattanInventoryDao
				.getLoadingManhattanInventoryJobs(manhattanChannel)
				.stream()
				.filter(j -> {
					Date now = new Date();
					Long millisecondsBetween = (now.getTime() - j.getCreatedDateTime().getTime());
					return j.getCurrentCount() >= j.getTotalCount()
							|| millisecondsBetween > manhattanInboundSettings
							.getJobCompletionTimeOutInMilliseconds();

				}).collect(Collectors.toList());
		if (manhattanInventoryJobs.size() > 1) {
			manhattanInventoryJobs.sort(Comparator.comparing(ManhattanInventoryJob::getCreatedDateTime));
		}
		if (manhattanInventoryJobs.isEmpty()) {
			return null;
		}
		return manhattanInventoryJobs.get(0);
	}

	@Override
	public void updateManhattanInventoryJob(ManhattanInventoryJob manhattanInventoryJob) {
		manhattanInventoryDao.updateManhattanInventoryJobStatus(manhattanInventoryJob);
	}

	@Override
	public void deleteManhattanInventoryJobData(int manhattanInventoryJobId) {
		manhattanInventoryDao.deleteManhattanInventoryJobData(manhattanInventoryJobId);
	}
}
