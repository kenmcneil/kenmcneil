package com.ferguson.cs.product.task.dy;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Configuration;

import com.ferguson.cs.task.batch.TaskBatchJobFactory;

@Configuration
public class DyFeedTaskConfiguration {
	private SqlSessionFactory coreSqlSessionFactory;
	private DyFeedSettings dyFeedSettings;

	private TaskBatchJobFactory taskBatchJobFactory;

}
