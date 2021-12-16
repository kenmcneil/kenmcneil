package com.ferguson.cs.dbtocsv;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(basePackageClasses = DBtoCsvConfiguration.class, annotationClass = Mapper.class)
public class DBtoCsvConfiguration {

	private static final String CORE_BASE_ALIAS_PACKAGE = "com.ferguson.cs.dbtocsv.model";

	@Bean
	@Primary
	@ConfigurationProperties("datasource.core")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource coreDataSource() {
		return dataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean
	@Primary
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
