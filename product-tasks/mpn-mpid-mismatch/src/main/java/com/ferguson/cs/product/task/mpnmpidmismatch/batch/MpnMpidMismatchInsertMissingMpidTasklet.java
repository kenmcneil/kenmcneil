package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.mpnmpidmismatch.data.MpnMpidMismatchService;

public class MpnMpidMismatchInsertMissingMpidTasklet implements Tasklet{
	private static final Logger LOGGER = LoggerFactory.getLogger(MpnMpidMismatchInsertMissingMpidTasklet.class);

	private final MpnMpidMismatchService mpnMpidMismatchService;

	public MpnMpidMismatchInsertMissingMpidTasklet(MpnMpidMismatchService mpnMpidMismatchService) {
		this.mpnMpidMismatchService = mpnMpidMismatchService;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		Integer updateRecordCount = mpnMpidMismatchService.insertMissingFeiMpidRecords();
		LOGGER.info("MpnMpidMismatchInsertMissingMpidTasklet - Inserted: {} missing feiMPID records", updateRecordCount);
		return RepeatStatus.FINISHED;
	}
}
