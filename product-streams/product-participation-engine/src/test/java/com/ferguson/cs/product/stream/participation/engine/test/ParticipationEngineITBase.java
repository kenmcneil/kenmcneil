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

import com.ferguson.cs.product.stream.participation.engine.test.model.CalculatedDiscountFixture;
import com.ferguson.cs.product.stream.participation.engine.test.model.ItemizedDiscountFixture;
import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.spring.LazyInitBeanFactoryPostProcessor;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Import(ParticipationEngineITBase.BaseParticipationTestConfiguration.class)
@Transactional
public abstract class ParticipationEngineITBase extends BaseTest {
	private final int[] TEST_UNIQUE_IDS = {100, 101, 102, 103, 104, 105};

	@Resource
	SqlSessionFactory sqlSessionFactory;

	@Autowired
	public ParticipationTestUtilities participationTestUtilities;

	@TestConfiguration
	public static class BaseParticipationTestConfiguration {
		@Bean
		public ParticipationTestUtilities participationTestUtilities(JdbcTemplate jdbcTemplate) {
			return new ParticipationTestUtilities(jdbcTemplate);
		}

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

	public CalculatedDiscountFixture percentCalculatedDiscount(int pricebookId, int percentDiscount) {
		return new CalculatedDiscountFixture(pricebookId, percentDiscount, true, null);
	}

	public CalculatedDiscountFixture amountCalculatedDiscount(int pricebookId, int amountDiscount) {
		return new CalculatedDiscountFixture(pricebookId, amountDiscount, false, null);
	}

	public ItemizedDiscountFixture itemizedDiscount(int uniqueId, double pricebook1Price, double pricebook22Price) {
		return new ItemizedDiscountFixture(uniqueId, pricebook1Price, pricebook22Price);
	}

	/**
	 * Return list of discontinued product unique ids that won't be used in real life. These probably won't be
	 * in the participationProduct table already when tests run.
	 */
	public int[] getSafeTestUniqueIds() {
		return TEST_UNIQUE_IDS;
	}
}
