package com.ferguson.cs.product.task.dbtocsv;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import com.ferguson.cs.product.task.dbtocsv.batch.DbToCsvProcessor;
import com.ferguson.cs.product.task.dbtocsv.batch.DbToCsvWriter;
import com.ferguson.cs.task.batch.TaskBatchJobFactory;


@Configuration
public class DBJobConfiguration {

	private final TaskBatchJobFactory taskBatchJobFactory;
	private final SqlSessionFactory sqlSessionFactory;
	private final PlatformTransactionManager transactionManager;

	public DBJobConfiguration(
			SqlSessionFactory sqlSessionFactory,
			PlatformTransactionManager transactionManager,
			TaskBatchJobFactory taskBatchJobFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.taskBatchJobFactory = taskBatchJobFactory;
		this.transactionManager = transactionManager;
	}

	@Bean
	public Job dbToCsvJob() {
		return this.taskBatchJobFactory
				.getJobBuilder("dbToCsvJob")
				.start(dbToCsvProcessDataStep())
				.build();
	}

	@Bean
	public Step dbToCsvProcessDataStep() {

		return taskBatchJobFactory.getStepBuilder("dbToCsvProcessDataStep")
				.<String, String>chunk(2)
				.reader(myBatisDataReader())
				.processor(dbToCsvProcessor())
				.writer(dbToCsvWriter())
				.transactionManager(this.transactionManager)
				.build();
	}

	@Bean
	@StepScope
	// Here we call MyBatis SQL directly.  But you could also specify a reader like we do for the
	// processor and writer if you have more complex logic like reading from a message queue or calling
	// and endpoint for data.
	public MyBatisCursorItemReader<String> myBatisDataReader() {

		MyBatisCursorItemReader<String> reader = new MyBatisCursorItemReader<>();
		// If you had params to pass to your SQL
		//		Map<String, Object> parameters = new HashMap<>();
		//		parameters.put("lastRanDate", mostRecentRun);
		//		reader.setParameterValues(parameters);
		reader.setQueryId("getSomeData");
		reader.setSqlSessionFactory(this.sqlSessionFactory);
		return reader;
	}

	@Bean
	@StepScope
	public ItemProcessor<String, String> dbToCsvProcessor() {
		return new DbToCsvProcessor();
	}

	@Bean
	@StepScope
	public ItemWriter<String> dbToCsvWriter() {
		return new DbToCsvWriter();
	}
}