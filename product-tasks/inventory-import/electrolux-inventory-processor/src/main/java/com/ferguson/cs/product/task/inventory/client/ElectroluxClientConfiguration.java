package com.ferguson.cs.product.task.inventory.client;

import org.springframework.context.annotation.Bean;

import feign.auth.BasicAuthRequestInterceptor;

public class ElectroluxClientConfiguration {

	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor("SVC-NA-IIB-U-BLDCOM", "noTNX+KBQl");
	}
}
