package com.ferguson.cs.product.stream.participation.engine.construct;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

@Configuration
@EnableMongoRepositories(basePackages = "com.ferguson.cs.product.stream.participation.engine.construct")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfig {

	private final MongoClient mongoClient;
	private final MongoProperties mongoProperties;
	private final MongoClientOptions options;
	private final Environment environment;

	@Value("${spring.data.mongodb.uri:}")
	private String mongoClientUserDataUri;

	public MongoConfig(MongoClient mongoClient, MongoProperties mongoProperties, ObjectProvider<MongoClientOptions> optionsProvider, Environment environment) {
		this.mongoClient = mongoClient;
		this.mongoProperties = mongoProperties;
		this.options = optionsProvider.getIfAvailable();
		this.environment = environment;
	}

	@Bean
	@Primary
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) {
		return new MongoTemplate(mongoDbFactory, converter);
	}

}
