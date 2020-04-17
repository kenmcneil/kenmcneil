package com.ferguson.cs.product.stream.participation.engine.test;

import javax.annotation.Resource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.stream.participation.engine.ParticipationProcessor;
import com.ferguson.cs.product.stream.participation.engine.ParticipationWriter;
import com.ferguson.cs.product.stream.participation.engine.construct.ConstructService;
import com.ferguson.cs.product.stream.participation.engine.data.ParticipationDao;
import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.spring.LazyInitBeanFactoryPostProcessor;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Transactional("coreTransactionManager")
@Import(BaseParticipationEngineIT.ParticipationTestConfiguration.class)
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseParticipationEngineIT extends BaseTest {
	@Resource
	SqlSessionFactory sqlSessionFactory;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@MockBean
	ConstructService contentService;

	@MockBean
	ParticipationWriter participationWriter;

	@Autowired
	@InjectMocks
	ParticipationProcessor participationProcessor;

	@Autowired
	ParticipationDao participationDao;

	@Autowired
	private ParticipationTestUtilities participationTestUtilities;

	@TestConfiguration
	@SpringBootApplication
	@EnableCircuitBreaker
	protected static class ParticipationTestConfiguration {
		@Bean
		public BeanFactoryPostProcessor lazyBeanPostProcessor() {
			return new LazyInitBeanFactoryPostProcessor();
		}

		@Bean
		public ParticipationTestUtilities participationTestUtilities(JdbcTemplate jdbcTemplate) {
			return new ParticipationTestUtilities(jdbcTemplate);
		}
	}

	/**
	 * Effectively disable query result caching by configuring the local cache to be per-statement.
	 */
	protected void disableLocalCache() {
		Configuration config = sqlSessionFactory.getConfiguration();
		config.setLocalCacheScope(LocalCacheScope.STATEMENT);
	}
}
