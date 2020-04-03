package com.ferguson.cs.product.stream.participation.engine;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(basePackageClasses = ParticipationEngineDataConfiguration.class, annotationClass = Mapper.class)
@EnableFeignClients
@EnableTransactionManagement
public class ParticipationEngineDataConfiguration {

	private static final String CORE_BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.stream.participation.model";

	@Bean
	@Primary
	@ConfigurationProperties("datasource.core")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties("datasource.core")
	public DataSource coreDataSource() {
		return dataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean(name="coreTransactionManager")
	public DataSourceTransactionManager coreTransactionManager() {
		return new DataSourceTransactionManager(coreDataSource());
	}

	@Bean(name="coreSqlSessionFactory")
	public SqlSessionFactory coreSqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		factory.setTypeAliasesPackage(CORE_BASE_ALIAS_PACKAGE);
		return factory.getObject();
	}
}
