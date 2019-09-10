package com.ferguson.cs.product;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import com.ferguson.cs.data.ApiDataAccessAutoConfiguration;

@SpringBootApplication
@EnableSpringDataWebSupport
@Import(ApiDataAccessAutoConfiguration.class)
public class ProductApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProductApplication.class).run(args);
	}

//	@Bean
//	public DefaultResponseBodyAdvice responseBodyAdvice() {
//		return new DefaultResponseBodyAdvice();
//	}
}
