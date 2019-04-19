package com.ferguson.cs.product.task.inventory.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ferguson.cs.product.task.inventory.ManhattanInboundSettings;
import com.ferguson.cs.product.task.inventory.dao.core.ManhattanInboundDao;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJobStatus;

@Service
public class ManhattanInboundProcessorServiceImpl implements ManhattanInboundProcessorService {

	private ManhattanInboundDao manhattanInboundDao;
	private ManhattanInboundSettings manhattanInboundSettings;


	@Autowired
	public void setManhattanInboundDao(ManhattanInboundDao manhattanInboundDao) {
		this.manhattanInboundDao = manhattanInboundDao;
	}

	@Autowired
	public void setManhattanInboundSettings(ManhattanInboundSettings manhattanInboundSettings) {
		this.manhattanInboundSettings = manhattanInboundSettings;
	}

	@Override
	public ManhattanIntakeJob getNewestReadyManhattanIntakeJob() {
		List<ManhattanIntakeJob> manhattanIntakeJobs = manhattanInboundDao.getManhattanIntakeJobs()
				.stream()
				.filter(j -> {
					Date now = new Date();
					Long millisecondsBetween = (now.getTime() - j.getCreatedDateTime().getTime());
					return j.getManhattanIntakeJobStatus() == ManhattanIntakeJobStatus.READY_FOR_PROCESSING
							|| (j
							.getManhattanIntakeJobStatus() == ManhattanIntakeJobStatus.LOADING && millisecondsBetween > manhattanInboundSettings
							.getJobCompletionTimeOutInMilliseconds());

				}).collect(Collectors.toList());
		if (manhattanIntakeJobs.size() > 1) {
			manhattanIntakeJobs.sort(Comparator.comparing(ManhattanIntakeJob::getCreatedDateTime));
			Collections.reverse(manhattanIntakeJobs);
		}
		if (manhattanIntakeJobs.isEmpty()) {
			return null;
		}
		return manhattanIntakeJobs.get(0);
	}

	@Override
	public void updateManhattanIntakeJob(ManhattanIntakeJob manhattanIntakeJob) {
		manhattanInboundDao.updateManhattanIntakeJobStatus(manhattanIntakeJob);
	}
}
