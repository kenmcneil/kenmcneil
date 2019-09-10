package com.ferguson.cs.data;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.mapping.context.PersistentEntities;

@Configuration
public class ApiDataAccessAutoConfiguration {

	@Bean
	public SimpleMappingContext mappingContext() {
		return new SimpleMappingContext();
	}

	@Bean
	public DataAccessHelper dataAccessHelper(ConversionService conversionService) {
		return new DataAccessHelperImpl(mappingContext(), auditingHandler(), conversionService);
	};

	@Bean
	public AuditingHandler auditingHandler() {
		AuditingHandler handler = new IsNewAwareAuditingHandler(PersistentEntities.of(mappingContext()));
		handler.setAuditorAware(() ->  Optional.of("default user"));
		return handler;
	};

	
}
