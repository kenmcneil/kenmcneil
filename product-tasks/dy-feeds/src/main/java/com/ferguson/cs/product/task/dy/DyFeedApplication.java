package com.ferguson.cs.product.task.dy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.dy")
public class DyFeedApplication {

	public static void main(String args[]) {
		SpringApplication.run(DyFeedApplication.class, args).close();
	}
}