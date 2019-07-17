package com.ferguson.cs.product.task.stylyze;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class StylyzeConfiguration {

	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.stylyze";
	private static final String BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.stylyze.data";

	@Bean
	@ConfigurationProperties("stylyze")
	public StylyzeSettings stylyzeSettings() {
		return new StylyzeSettings();
	}

	@MapperScan(basePackages = StylyzeConfiguration.BASE_MAPPER_PACKAGE, annotationClass = Mapper.class, sqlSessionFactoryRef = "reporterSqlSessionFactory")
	@Configuration
	protected static class ReporterDataSourceConfiguration {
		// --------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to
		// alias the data source
		// so that the task batch auto configuration works properly.
		// --------------------------------------------------------------------------------------------------
		@Bean(name = "reporterDataSource")
		@ConfigurationProperties(prefix = "datasource.reporter")
		public DataSource reporterDataSource() {
			return DataSourceBuilder.create().build();
		}

		@Bean(name = "reporterTransactionManager")
		public DataSourceTransactionManager reporterTransactionManager() {
			return new DataSourceTransactionManager(reporterDataSource());
		}

		@Bean(name = "reporterSqlSessionFactory")
		public SqlSessionFactory reporterSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(reporterDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}

	@MapperScan(basePackages = StylyzeConfiguration.BASE_MAPPER_PACKAGE, annotationClass = Mapper.class, sqlSessionFactoryRef = "coreSqlSessionFactory")
	@Configuration
	protected static class CoreDataSourceConfiguration {

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
}
