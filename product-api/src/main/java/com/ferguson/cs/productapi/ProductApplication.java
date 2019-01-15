package com.ferguson.cs.productapi;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ProductApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProductApplication.class).run(args);
	}
}
