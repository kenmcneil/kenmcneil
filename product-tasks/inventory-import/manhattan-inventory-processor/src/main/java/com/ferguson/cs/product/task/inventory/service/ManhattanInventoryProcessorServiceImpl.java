package com.ferguson.cs.product.task.inventory.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ferguson.cs.product.task.inventory.ManhattanInboundSettings;
import com.ferguson.cs.product.task.inventory.dao.core.ManhattanInventoryDao;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryLocationData;

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
				.getLoadingManhattanInventoryJobs(manhattanChannel);

		if (manhattanInventoryJobs.size() > 1) {
			manhattanInventoryJobs.sort(Comparator.comparing(ManhattanInventoryJob::getCreatedDateTime));
		}

		ManhattanInventoryJob oldestReadyJob = null;
		Date now = new Date();
		for (ManhattanInventoryJob manhattanInventoryJob : manhattanInventoryJobs) {
			List<ManhattanInventoryLocationData> manhattanInventoryLocationDataList = manhattanInventoryDao
					.getManhattanInventoryLocationDataForJob(manhattanInventoryJob.getId());

			//No location data means the job is not a ready job
			if (manhattanInventoryLocationDataList == null || manhattanInventoryLocationDataList.isEmpty()) {
				continue;
			}


			if (manhattanInventoryLocationDataList.size() >= manhattanInventoryJob.getTotalCount()) {
				//Count the number of locations that are ready, if all are, this is our oldest ready job
				int readyLocations = 0;
				for (ManhattanInventoryLocationData manhattanInventoryLocationData : manhattanInventoryLocationDataList) {
					if (manhattanInventoryLocationData.getCurrentItemPageCount() >= manhattanInventoryLocationData
							.getTotalItemPageCount()) {
						readyLocations++;
					} else {
						//If any locations aren't ready, the job is not ready
						break;
					}
				}
				if (readyLocations >= manhattanInventoryJob.getTotalCount()) {
					manhattanInventoryJob.setDataIsComplete(true);
					oldestReadyJob = manhattanInventoryJob;
					break;
				}
			}

			//Sort location data by modified date time in descending order
			manhattanInventoryLocationDataList
					.sort((o1, o2) -> o2.getModifiedDateTime().compareTo(o1.getModifiedDateTime()));

			//If the newest modified date is longer from now than the timeout, this job is the "oldest ready job".
			if (now.getTime() - manhattanInventoryLocationDataList.get(0).getModifiedDateTime()
					.getTime() > manhattanInboundSettings.getJobCompletionTimeOutInMilliseconds()) {
				manhattanInventoryJob.setDataIsComplete(false);
				oldestReadyJob = manhattanInventoryJob;
				break;
			}


		}

		return oldestReadyJob;
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
