package com.ferguson.cs.product.task.dy;

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
import org.springframework.integration.annotation.IntegrationComponentScan;

@Configuration
@IntegrationComponentScan
public class DyFeedConfiguration {
	static final String CORE_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.dy.dao.core";
	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.dy.model";

	@MapperScan(basePackages= DyFeedConfiguration.CORE_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "coreSqlSessionFactory")
	@Configuration
	public static class CoreDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the core data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSourceProperties coreDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSource coreDataSource() {
			return coreDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		@Primary
		public SqlSessionFactory coreSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(coreDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}
}
