package com.ferguson.cs.product.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.ferguson.cs.product.dao.attribute.AttributeDataAccess;
import com.ferguson.cs.product.dao.taxonomy.TaxonomyDataAccess;
import com.ferguson.cs.product.test.attribute.AttributeTestUtilities;
import com.ferguson.cs.product.test.taxonomy.TaxonomyTestUtilities;
import com.ferguson.cs.test.BaseTest;
import com.ferguson.cs.test.utilities.spring.LazyInitBeanFactoryPostProcessor;

@SpringBootTest( webEnvironment = WebEnvironment.NONE)
@Import(BaseProductIT.ProductTestConfiguration.class)
@Transactional
public abstract class BaseProductIT extends BaseTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TaxonomyTestUtilities taxonomyTestUtilities;

	@Autowired
	private AttributeTestUtilities attributeTestUtilities;

	protected JdbcTemplate jdbcTemplate() {
		return jdbcTemplate;
	}

	protected TaxonomyTestUtilities taxonomyTestUtilities() {
		return taxonomyTestUtilities;
	}
	protected AttributeTestUtilities attributeTestUtilities() {
		return attributeTestUtilities;
	}


	@TestConfiguration
	protected static class ProductTestConfiguration {
		@Bean
		public BeanFactoryPostProcessor lazyBeanPostProcessor() {
			return new LazyInitBeanFactoryPostProcessor();
		}

		@Bean
		public ConversionService testConversionService() {
			return ApplicationConversionService.getSharedInstance();
		}

		@Bean
		public AttributeTestUtilities attributeTestUtilities(AttributeDataAccess attributeDataAccess) {
			return new AttributeTestUtilities(attributeDataAccess);
		}

		@Bean
		public TaxonomyTestUtilities taxonomyTestUtilities(TaxonomyDataAccess taxonomyDataAccess) {
			return new TaxonomyTestUtilities(taxonomyDataAccess);
		}

	}

}
