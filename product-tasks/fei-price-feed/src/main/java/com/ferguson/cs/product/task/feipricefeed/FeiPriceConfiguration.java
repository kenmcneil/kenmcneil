package com.ferguson.cs.product.task.feipricefeed;

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

@Configuration
public class FeiPriceConfiguration {

	private static final String BASE_ALIAS_PACKAGE = "com.ferguson.cs.product.task.feipricefeed.model";
	private static final String BASE_MAPPER_PACKAGE = "com.ferguson.cs.product.task.feipricefeed.data";
	private static final String FEI_UPLOAD_SFTP_CHANNEL = "feiUploadSftpChannel";
	private final FeiPriceSettings feiPriceSettings;

	public FeiPriceConfiguration(FeiPriceSettings feiPriceSettings) {
		this.feiPriceSettings = feiPriceSettings;
	}

	@MapperScan(basePackages = FeiPriceConfiguration.BASE_MAPPER_PACKAGE, annotationClass = Mapper.class, sqlSessionFactoryRef = "reporterSqlSessionFactory")
	@Configuration
	protected static class ReporterDataSourceConfiguration {
		//--------------------------------------------------------------------------------------------------
		// Setup the reporter data source and then wire up a mybatis sql map. We have to alias the data source
		// so that the task batch auto configuration works properly.
		//--------------------------------------------------------------------------------------------------
		@Bean
		@ConfigurationProperties(prefix = "datasource.reporter")
		@Primary
		public DataSourceProperties reporterDataSourceProperties() {
			return new DataSourceProperties();
		}

		@Bean
		@Primary
		public DataSource reporterDataSource() {
			return reporterDataSourceProperties().initializeDataSourceBuilder().build();
		}

		@Bean
		@Primary
		public SqlSessionFactory reporterSqlSessionFactory(@Value("mybatis.type-aliases-package:") String typeHandlerPackage) throws Exception {
			SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
			factory.setDataSource(reporterDataSource());
			factory.setVfs(SpringBootVFS.class);
			factory.setTypeAliasesPackage(BASE_ALIAS_PACKAGE);
			factory.setTypeHandlersPackage(typeHandlerPackage);
			return factory.getObject();
		}
	}
}
