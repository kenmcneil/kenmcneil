package com.ferguson.cs.product.task.wiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ferguson.cs.product.task.wiser")
public class WiserFeedApplication {

	public static void main(String args[]) {
		SpringApplication.run(WiserFeedApplication.class, args).close();
	}
}