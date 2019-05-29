package com.ferguson.cs.vendor.quickship;

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
@MapperScan(basePackages = VendorQuickShipConfiguration.BASE_MAPPER_PACKAGE, annotationClass = Mapper.class, sqlSessionFactoryRef = "coreSqlSessionFactory")
public class VendorQuickShipConfiguration {
	protected static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.vendor.quickship";
	protected static final String BASE_MAPPER_PACKAGE = "com.ferguson.cs.vendor.quickship.service";

	@Bean
	@ConfigurationProperties("datasource.core")
	@Primary
	public DataSourceProperties coreDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource coreDataSource() {
		return coreDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean(name = "coreTransactionManager")
	public DataSourceTransactionManager coreTransactionManager() {
		return new DataSourceTransactionManager(coreDataSource());
	}

	@Primary
	@Bean(name = "coreSqlSessionFactory")
	public SqlSessionFactory coreSqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
		factory.setVfs(SpringBootVFS.class);
		return factory.getObject();
	}

}
