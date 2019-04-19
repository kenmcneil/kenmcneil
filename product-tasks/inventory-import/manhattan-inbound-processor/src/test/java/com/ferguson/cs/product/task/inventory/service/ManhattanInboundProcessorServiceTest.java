package com.ferguson.cs.product.task.inventory.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import com.ferguson.cs.product.task.inventory.ManhattanInboundSettings;
import com.ferguson.cs.product.task.inventory.dao.core.ManhattanInboundDao;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanIntakeJobStatus;
import com.ferguson.cs.utilities.DateUtils;

public class ManhattanInboundProcessorServiceTest {
	@Mock
	private ManhattanInboundDao manhattanInboundDao;

	@Mock
	private ManhattanInboundSettings manhattanInboundSettings;

	@InjectMocks
	@Spy
	private ManhattanInboundProcessorServiceImpl manhattanInboundProcessorService = new ManhattanInboundProcessorServiceImpl();

	@Before
	public void setUpBefore() {
		MockitoAnnotations.initMocks(this);
		when(manhattanInboundSettings.getJobCompletionTimeOutInMilliseconds()).thenReturn(60000L);
	}

	@Test
	public void testGetNewestReadyManhattanIntakeJob_readyForProcessingJob() {
		Date now = new Date();
		Date mostRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS,-20);
		Date lessRecentCreatedTime = DateUtils.addUnitToDate(now,ChronoUnit.SECONDS,-30);

		ManhattanIntakeJob mostRecentMatchingJob = new ManhattanIntakeJob();
		mostRecentMatchingJob.setId(1);
		mostRecentMatchingJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.READY_FOR_PROCESSING);
		mostRecentMatchingJob.setTransactionNumber("foo1");
		mostRecentMatchingJob.setCreatedDateTime(mostRecentCreatedTime);

		ManhattanIntakeJob lessRecentMatchingJob = new ManhattanIntakeJob();
		lessRecentMatchingJob.setId(2);
		lessRecentMatchingJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.READY_FOR_PROCESSING);
		lessRecentMatchingJob.setTransactionNumber("foo2");
		lessRecentMatchingJob.setCreatedDateTime(lessRecentCreatedTime);

		ManhattanIntakeJob nonMatchingControl = new ManhattanIntakeJob();
		nonMatchingControl.setId(3);
		nonMatchingControl.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.LOADING);
		nonMatchingControl.setTransactionNumber("foo3");
		nonMatchingControl.setCreatedDateTime(mostRecentCreatedTime);

		List<ManhattanIntakeJob> jobList = new ArrayList<>();
		jobList.add(lessRecentMatchingJob);
		jobList.add(mostRecentMatchingJob);
		jobList.add(nonMatchingControl);

		when(manhattanInboundDao.getManhattanIntakeJobs()).thenReturn(jobList);

		ManhattanIntakeJob manhattanIntakeJob = manhattanInboundProcessorService.getNewestReadyManhattanIntakeJob();

		assertThat(manhattanIntakeJob).isNotNull();
		assertThat(manhattanIntakeJob).isSameAs(mostRecentMatchingJob);
	}

	@Test
	public void testGetNewestReadyManhattanIntakeJob_jobPastTimeOut() {
		Date now = new Date();
		Date mostRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS,-20);
		Date lessRecentCreatedTime = DateUtils.addUnitToDate(now,ChronoUnit.SECONDS,-70);

		ManhattanIntakeJob mostRecentLoadingJob = new ManhattanIntakeJob();
		mostRecentLoadingJob.setId(1);
		mostRecentLoadingJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.LOADING);
		mostRecentLoadingJob.setTransactionNumber("foo1");
		mostRecentLoadingJob.setCreatedDateTime(mostRecentCreatedTime);

		ManhattanIntakeJob loadingJobPastTimeout = new ManhattanIntakeJob();
		loadingJobPastTimeout.setId(2);
		loadingJobPastTimeout.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.LOADING);
		loadingJobPastTimeout.setTransactionNumber("foo2");
		loadingJobPastTimeout.setCreatedDateTime(lessRecentCreatedTime);

		ManhattanIntakeJob nonMatchingControl = new ManhattanIntakeJob();
		nonMatchingControl.setId(3);
		nonMatchingControl.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.LOADING);
		nonMatchingControl.setTransactionNumber("foo3");
		nonMatchingControl.setCreatedDateTime(mostRecentCreatedTime);

		List<ManhattanIntakeJob> jobList = new ArrayList<>();
		jobList.add(loadingJobPastTimeout);
		jobList.add(mostRecentLoadingJob);
		jobList.add(nonMatchingControl);

		when(manhattanInboundDao.getManhattanIntakeJobs()).thenReturn(jobList);

		ManhattanIntakeJob manhattanIntakeJob = manhattanInboundProcessorService.getNewestReadyManhattanIntakeJob();

		assertThat(manhattanIntakeJob).isNotNull();
		assertThat(manhattanIntakeJob).isSameAs(loadingJobPastTimeout);
	}

	@Test
	public void testGetNewestReadyManhattanIntakeJob_noMatchingJobs() {
		Date now = new Date();
		Date mostRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS,-20);
		Date lessRecentCreatedTime = DateUtils.addUnitToDate(now,ChronoUnit.SECONDS,-70);

		ManhattanIntakeJob completeJob = new ManhattanIntakeJob();
		completeJob.setId(1);
		completeJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.COMPLETE);
		completeJob.setTransactionNumber("foo1");
		completeJob.setCreatedDateTime(lessRecentCreatedTime);

		ManhattanIntakeJob loadingJob = new ManhattanIntakeJob();
		loadingJob.setId(2);
		loadingJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.LOADING);
		loadingJob.setTransactionNumber("foo2");
		loadingJob.setCreatedDateTime(mostRecentCreatedTime);

		ManhattanIntakeJob failedJob = new ManhattanIntakeJob();
		failedJob.setId(3);
		failedJob.setManhattanIntakeJobStatus(ManhattanIntakeJobStatus.FAILED);
		failedJob.setTransactionNumber("foo3");
		failedJob.setCreatedDateTime(lessRecentCreatedTime);

		List<ManhattanIntakeJob> jobList = new ArrayList<>();
		jobList.add(loadingJob);
		jobList.add(completeJob);
		jobList.add(failedJob);

		when(manhattanInboundDao.getManhattanIntakeJobs()).thenReturn(jobList);

		ManhattanIntakeJob manhattanIntakeJob = manhattanInboundProcessorService.getNewestReadyManhattanIntakeJob();

		assertThat(manhattanIntakeJob).isNull();
	}
}
