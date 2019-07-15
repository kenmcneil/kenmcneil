package com.ferguson.cs.product.task.brand;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(basePackages=ProductDistributionCommonAutoConfiguration.INTEGRATION_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef="integrationSqlSessionFactory")
public class ProductDistributionCommonAutoConfiguration {

	protected static final String INTEGRATION_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.brand.dao";
	protected static final String CORE_BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.brand";

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "datasource.integration")
	public DataSourceProperties integrationDataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean(name = {"integrationDataSource"}, destroyMethod="")
	@Primary
	@ConfigurationProperties(prefix = "datasource.integration")
	public DataSource integrationDataSource() {
		return integrationDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean(name = "integrationTransactionManager")
	public DataSourceTransactionManager integrationTransactionManager() {
		return new DataSourceTransactionManager(integrationDataSource());
	}

	@Bean(name = "integrationSqlSessionFactory")
	public SqlSessionFactory integrationSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {

		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(integrationDataSource());
		factory.setVfs(SpringBootVFS.class);
		factory.setTypeAliasesPackage(CORE_BASE_ALIAS_PACKAGE);
		factory.setTypeHandlersPackage(typeHandlerPackage);
		return factory.getObject();
	}

}
