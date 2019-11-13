package com.ferguson.cs.product.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnProperty(name = "build.swagger.endpoints.enabled", havingValue = "true", matchIfMissing = true)
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage("com.ferguson.cs.product.api"))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(new ApiInfoBuilder()
				.title("Commerce Services Product Services API")
				.contact(new Contact(null, null, "sodev-developer@build.com"))
				.build());
	}
}
