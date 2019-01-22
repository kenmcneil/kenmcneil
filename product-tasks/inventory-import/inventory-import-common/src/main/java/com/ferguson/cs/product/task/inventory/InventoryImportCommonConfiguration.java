package com.ferguson.cs.product.task.inventory;


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
public class InventoryImportCommonConfiguration {

	protected static final String REPORTER_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.inventory.dao.reporter";
	protected static final String PDM_BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.inventory.dao.pdm";
	protected static final String BASE_ALIAS_PAKCAGE = "com.ferguson.cs.product.task.inventory.model";
	public static final int FTP_PORT = 21;
	public static final int SFTP_PORT = 22;

	@MapperScan(basePackages= InventoryImportCommonConfiguration.REPORTER_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "reporterSqlSessionFactory")
	@Configuration
	public static class ReporterDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@Primary
		@ConfigurationProperties(prefix = "datasource.reporter")
		public DataSourceProperties reporterDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@Primary
		@ConfigurationProperties(prefix = "datasource.reporter")
		public DataSource reporterDataSource() {
			return reporterDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		public DataSourceTransactionManager reporterTransactionManager() {
			return new DataSourceTransactionManager(reporterDataSource());
		}

		@Bean
		public SqlSessionFactory reporterSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(reporterDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MapperScan(basePackages= InventoryImportCommonConfiguration.PDM_BASE_MAPPER_PACKAGE, annotationClass=Mapper.class, sqlSessionFactoryRef = "pdmSqlSessionFactory")
	@Configuration
	public static class PdmDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the pdm data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.pdm")
		public DataSourceProperties pdmDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@ConfigurationProperties(prefix = "datasource.pdm")
		public DataSource pdmDataSource() {
			return pdmDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		@Primary
		public DataSourceTransactionManager pdmTransactionManager() {
			return new DataSourceTransactionManager(pdmDataSource());
		}

		@Bean
		@Primary
		public SqlSessionFactory pdmSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(pdmDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PAKCAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}




}
