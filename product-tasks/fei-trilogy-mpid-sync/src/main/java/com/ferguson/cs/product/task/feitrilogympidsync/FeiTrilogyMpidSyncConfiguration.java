package com.ferguson.cs.product.task.feitrilogympidsync;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeiTrilogyMpidSyncConfiguration {

	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.feitrilogympidsync.model";
	private static final String BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.feitrilogympidsync.data";


	@MapperScan(basePackages = BASE_MAPPER_PACKAGE, annotationClass = Mapper.class, sqlSessionFactoryRef = "sqlSessionFactory")
	@Configuration
	protected static class DataSourceConfiguration {
		// --------------------------------------------------------------------------------------------------
		// Setup the task data source and then wire up a mybatis sql map. We have to
		// alias the data source
		// so that the task batch auto configuration works properly.
		// --------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.core")
		@Primary
		public DataSourceProperties dataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@Primary
		@Qualifier("datasource")
		public DataSource dataSource() {
			return dataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		@Primary
		public SqlSessionFactory sqlSessionFactory(
				@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(dataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}
}
