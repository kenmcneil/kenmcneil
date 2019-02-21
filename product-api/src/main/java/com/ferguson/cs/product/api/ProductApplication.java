package com.ferguson.cs.product.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import com.ferguson.cs.server.common.response.DefaultResponseBodyAdvice;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories
@EnableSpringDataWebSupport
public class ProductApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProductApplication.class).run(args);
	}

	@Bean
	public DefaultResponseBodyAdvice responseBodyAdvice() {
		return new DefaultResponseBodyAdvice();
	}
}
