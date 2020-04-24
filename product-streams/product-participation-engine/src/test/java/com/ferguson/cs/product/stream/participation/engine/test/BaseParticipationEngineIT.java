package com.ferguson.cs.product.stream.participation.engine.test;

import javax.annotation.Resource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.spring.LazyInitBeanFactoryPostProcessor;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Import(BaseParticipationEngineIT.ParticipationTestConfiguration.class)
@Transactional("coreTransactionManager")
public abstract class BaseParticipationEngineIT extends BaseTest {
	@Resource
	SqlSessionFactory sqlSessionFactory;

	@Autowired
	public JdbcTemplate jdbcTemplate;

	@Autowired
	public ParticipationTestUtilities participationTestUtilities;

	@TestConfiguration
	public static class ParticipationTestConfiguration {
		@Bean
		public BeanFactoryPostProcessor lazyBeanPostProcessor() {
			return new LazyInitBeanFactoryPostProcessor();
		}
	}

	@Before
	public void before() {
		disableLocalCache();
	}

	/**
	 * Effectively disable query result caching by configuring the local cache to be per-statement.
	 */
	public void disableLocalCache() {
		Configuration config = sqlSessionFactory.getConfiguration();
		config.setLocalCacheScope(LocalCacheScope.STATEMENT);
	}
}
