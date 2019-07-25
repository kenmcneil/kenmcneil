package com.ferguson.cs.product.task.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import com.ferguson.cs.product.task.inventory.dao.core.ManhattanInventoryDao;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanChannel;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJob;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryJobStatus;
import com.ferguson.cs.product.task.inventory.model.manhattan.ManhattanInventoryLocationData;
import com.ferguson.cs.utilities.DateUtils;

public class ManhattanInventoryProcessorServiceTest {
	@Mock
	private ManhattanInventoryDao manhattanInventoryDao;

	@Mock
	private ManhattanInboundSettings manhattanInboundSettings;

	@InjectMocks
	@Spy
	private ManhattanInventoryProcessorServiceImpl manhattanInventoryProcessorService = new ManhattanInventoryProcessorServiceImpl();

	@Before
	public void setUpBefore() {
		MockitoAnnotations.initMocks(this);
		when(manhattanInboundSettings.getJobCompletionTimeOutInMilliseconds()).thenReturn(60000L);
	}

	@Test
	public void testGetNewestReadyManhattanInventoryJob_readyForProcessingJob() {
		Date now = new Date();
		Date mostRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS, -20);
		Date lessRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS, -30);

		ManhattanInventoryJob mostRecentMatchingJob = new ManhattanInventoryJob();
		mostRecentMatchingJob.setId(1);
		mostRecentMatchingJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.LOADING);
		mostRecentMatchingJob.setTransactionNumber("foo1");
		mostRecentMatchingJob.setCreatedDateTime(mostRecentCreatedTime);
		mostRecentMatchingJob.setManhattanChannel(ManhattanChannel.BUILD);
		mostRecentMatchingJob.setTotalCount(3);

		ManhattanInventoryJob lessRecentMatchingJob = new ManhattanInventoryJob();
		lessRecentMatchingJob.setId(2);
		lessRecentMatchingJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.LOADING);
		lessRecentMatchingJob.setTransactionNumber("foo2");
		lessRecentMatchingJob.setCreatedDateTime(lessRecentCreatedTime);
		lessRecentMatchingJob.setManhattanChannel(ManhattanChannel.BUILD);
		lessRecentMatchingJob.setTotalCount(2);

		ManhattanInventoryJob nonMatchingControl = new ManhattanInventoryJob();
		nonMatchingControl.setId(3);
		nonMatchingControl.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.LOADING);
		nonMatchingControl.setTransactionNumber("foo3");
		nonMatchingControl.setCreatedDateTime(mostRecentCreatedTime);
		nonMatchingControl.setManhattanChannel(ManhattanChannel.BUILD);
		nonMatchingControl.setTotalCount(3);

		List<ManhattanInventoryJob> jobList = new ArrayList<>();
		jobList.add(lessRecentMatchingJob);
		jobList.add(mostRecentMatchingJob);
		jobList.add(nonMatchingControl);

		ManhattanInventoryLocationData manhattanInventoryLocationData1 = new ManhattanInventoryLocationData();
		manhattanInventoryLocationData1.setCreatedDateTime(new Date());
		manhattanInventoryLocationData1.setModifiedDateTime(new Date());
		manhattanInventoryLocationData1.setCurrentCount(0);
		manhattanInventoryLocationData1.setCurrentItemPageCount(2);
		manhattanInventoryLocationData1.setTotalItemPageCount(2);
		manhattanInventoryLocationData1.setManhattanInventoryJobId(2);
		manhattanInventoryLocationData1.setId(1);

		ManhattanInventoryLocationData manhattanInventoryLocationData2 = new ManhattanInventoryLocationData();
		manhattanInventoryLocationData2.setCreatedDateTime(new Date());
		manhattanInventoryLocationData2.setModifiedDateTime(new Date());
		manhattanInventoryLocationData2.setCurrentCount(1);
		manhattanInventoryLocationData2.setCurrentItemPageCount(2);
		manhattanInventoryLocationData2.setTotalItemPageCount(2);
		manhattanInventoryLocationData2.setManhattanInventoryJobId(2);
		manhattanInventoryLocationData2.setId(2);

		List<ManhattanInventoryLocationData> manhattanInventoryLocationDataList = new ArrayList<>();
		manhattanInventoryLocationDataList.add(manhattanInventoryLocationData1);
		manhattanInventoryLocationDataList.add(manhattanInventoryLocationData2);



		when(manhattanInventoryDao.getLoadingManhattanInventoryJobs(any())).thenReturn(jobList);
		when(manhattanInventoryDao.getManhattanInventoryLocationDataForJob(2)).thenReturn(manhattanInventoryLocationDataList);

		ManhattanInventoryJob manhattanInventoryJob = manhattanInventoryProcessorService
				.getOldestReadyManhattanInventoryJob(ManhattanChannel.BUILD);

		assertThat(manhattanInventoryJob).isNotNull();
		assertThat(manhattanInventoryJob).isSameAs(lessRecentMatchingJob);
	}

	@Test
	public void testGetNewestReadyManhattanInventoryJob_jobPastTimeOut() {
		Date now = new Date();
		Date mostRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS, -20);
		Date lessRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS, -70);

		ManhattanInventoryJob mostRecentLoadingJob = new ManhattanInventoryJob();
		mostRecentLoadingJob.setId(1);
		mostRecentLoadingJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.LOADING);
		mostRecentLoadingJob.setTransactionNumber("foo1");
		mostRecentLoadingJob.setCreatedDateTime(mostRecentCreatedTime);
		mostRecentLoadingJob.setTotalCount(3);
		mostRecentLoadingJob.setManhattanChannel(ManhattanChannel.BUILD);

		ManhattanInventoryJob loadingJobPastTimeout = new ManhattanInventoryJob();
		loadingJobPastTimeout.setId(2);
		loadingJobPastTimeout.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.LOADING);
		loadingJobPastTimeout.setTransactionNumber("foo2");
		loadingJobPastTimeout.setCreatedDateTime(lessRecentCreatedTime);
		loadingJobPastTimeout.setTotalCount(3);
		loadingJobPastTimeout.setManhattanChannel(ManhattanChannel.BUILD);

		List<ManhattanInventoryJob> jobList = new ArrayList<>();
		jobList.add(loadingJobPastTimeout);
		jobList.add(mostRecentLoadingJob);

		ManhattanInventoryLocationData manhattanInventoryLocationData1 = new ManhattanInventoryLocationData();
		manhattanInventoryLocationData1.setCreatedDateTime(new Date());
		manhattanInventoryLocationData1.setModifiedDateTime(lessRecentCreatedTime);
		manhattanInventoryLocationData1.setCurrentCount(0);
		manhattanInventoryLocationData1.setCurrentItemPageCount(1);
		manhattanInventoryLocationData1.setTotalItemPageCount(2);
		manhattanInventoryLocationData1.setManhattanInventoryJobId(2);
		manhattanInventoryLocationData1.setId(1);

		List<ManhattanInventoryLocationData> manhattanInventoryLocationDataList = new ArrayList<>();
		manhattanInventoryLocationDataList.add(manhattanInventoryLocationData1);

		when(manhattanInventoryDao.getLoadingManhattanInventoryJobs(any())).thenReturn(jobList);
		when(manhattanInventoryDao.getManhattanInventoryLocationDataForJob(2)).thenReturn(manhattanInventoryLocationDataList);

		ManhattanInventoryJob manhattanInventoryJob = manhattanInventoryProcessorService
				.getOldestReadyManhattanInventoryJob(ManhattanChannel.BUILD);

		assertThat(manhattanInventoryJob).isNotNull();
		assertThat(manhattanInventoryJob).isSameAs(loadingJobPastTimeout);
	}

	@Test
	public void testGetNewestReadyManhattanInventoryJob_noMatchingJobs() {
		Date now = new Date();
		Date mostRecentCreatedTime = DateUtils.addUnitToDate(now, ChronoUnit.SECONDS, -20);

		ManhattanInventoryJob loadingJob = new ManhattanInventoryJob();
		loadingJob.setId(2);
		loadingJob.setManhattanInventoryJobStatus(ManhattanInventoryJobStatus.LOADING);
		loadingJob.setTransactionNumber("foo2");
		loadingJob.setCreatedDateTime(mostRecentCreatedTime);
		loadingJob.setTotalCount(3);
		loadingJob.setManhattanChannel(ManhattanChannel.BUILD);

		List<ManhattanInventoryJob> jobList = new ArrayList<>();
		jobList.add(loadingJob);

		ManhattanInventoryLocationData manhattanInventoryLocationData1 = new ManhattanInventoryLocationData();
		manhattanInventoryLocationData1.setCreatedDateTime(new Date());
		manhattanInventoryLocationData1.setModifiedDateTime(new Date());
		manhattanInventoryLocationData1.setCurrentCount(0);
		manhattanInventoryLocationData1.setCurrentItemPageCount(1);
		manhattanInventoryLocationData1.setTotalItemPageCount(2);
		manhattanInventoryLocationData1.setManhattanInventoryJobId(2);
		manhattanInventoryLocationData1.setId(1);

		List<ManhattanInventoryLocationData> manhattanInventoryLocationDataList = new ArrayList<>();
		manhattanInventoryLocationDataList.add(manhattanInventoryLocationData1);

		when(manhattanInventoryDao.getLoadingManhattanInventoryJobs(any())).thenReturn(jobList);
		when(manhattanInventoryDao.getManhattanInventoryLocationDataForJob(2)).thenReturn(manhattanInventoryLocationDataList);

		ManhattanInventoryJob manhattanInventoryJob = manhattanInventoryProcessorService
				.getOldestReadyManhattanInventoryJob(ManhattanChannel.BUILD);

		assertThat(manhattanInventoryJob).isNull();
	}
}
