package com.ferguson.cs.product.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories
public class ProductApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProductApplication.class).run(args);
	}
}
