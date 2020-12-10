package com.ferguson.cs.product.task.feitrilogympidsync;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.product.task.feitrilogympidsync.batch.FeiTrilogyMpidSyncWriter;
import com.ferguson.cs.product.task.feitrilogympidsync.data.FeiTrilogyMpidSyncService;
import com.ferguson.cs.product.task.feitrilogympidsync.model.FeiTrilogyMpidSync;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class FeiTrilogyMpidSyncTaskConfiguration {

	private final TaskBatchJobFactory taskBatchJobFactory;
	private final SqlSessionFactory sqlSessionFactory;
	private final FeiTrilogyMpidSyncService feiTrilogyMpidSyncService;

	public FeiTrilogyMpidSyncTaskConfiguration(
			TaskBatchJobFactory taskBatchJobFactory,
			SqlSessionFactory sqlSessionFactory,
			FeiTrilogyMpidSyncService feiTrilogyMpidSyncService) {
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.sqlSessionFactory = sqlSessionFactory;
		this.feiTrilogyMpidSyncService = feiTrilogyMpidSyncService;
	}

	/*
	 * Job
	 */
	@Bean
	public Job feiTrilogyMpidSyncJob(
			Step syncTrilogyMpids) {
		return taskBatchJobFactory.getJobBuilder("feiTrilogyMpidSyncJob")
				.start(syncTrilogyMpids).build();
	}

	/**
	 * Mybatis reader returns all feiMPID records with a matching MPID in the trilogyMPID table
	 * where feiMPID inTrilogy flag is false.  These will all get the inTrilogy flag updated to 1
	 * in the writer
	 * @return
	 */
	@Bean
	public MyBatisCursorItemReader<FeiTrilogyMpidSync> feiTrilogyMpidSyncReader() {
		MyBatisCursorItemReader<FeiTrilogyMpidSync> reader = new MyBatisCursorItemReader<>();
		reader.setQueryId("getTrilogyMpidsToUpdate");
		reader.setSqlSessionFactory(sqlSessionFactory);
		return reader;
	}

	/*
	 * Writer
	 * Updates the feiMPID inTrilogy flag to 1 for the MPID's returned by the reader
	 */
	@Bean
	public ItemWriter<FeiTrilogyMpidSync> feiTrilogyMpidSyncWriter() {
		return new FeiTrilogyMpidSyncWriter(feiTrilogyMpidSyncService);
	}

	@Bean
	public Step syncTrilogyMpids(MyBatisCursorItemReader<FeiTrilogyMpidSync> feiTrilogyMpidSyncReader,
			ItemWriter<FeiTrilogyMpidSync> feiTrilogyMpidSyncWriter) {
		return taskBatchJobFactory.getStepBuilder("feiTrilogyMpidSyncWriter").<FeiTrilogyMpidSync, FeiTrilogyMpidSync>chunk(1000)
				.reader(feiTrilogyMpidSyncReader()).writer(feiTrilogyMpidSyncWriter).build();
	}

}
